package com.koldyr.genealogy.services

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.koldyr.genealogy.dto.Credentials
import com.koldyr.genealogy.dto.LibraryUser
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.RoleRepository
import com.koldyr.genealogy.persistence.UserRepository

/**
 * Description of class UserServiceImpl
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    @Value("\${spring.security.token.exp}") val expiration: String,
    @Value("\${spring.security.secret}") secret: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.jws-algorithms}") algorithm: String
) : UserService {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    private val signer = MACSigner(secret.toByteArray())

    private val header = JWSHeader.Builder(JWSAlgorithm.parse(algorithm)).contentType("text/plain").build()

    @Transactional
    override fun create(user: User) {
        if (user.email.isBlank() || user.password.isBlank() || user.name.isBlank() || user.surName.isBlank()) {
            throw ResponseStatusException(BAD_REQUEST, "invalid data")
        }
        if (userRepository.findByEmail(user.email).isPresent) {
            throw ResponseStatusException(BAD_REQUEST, "User already registered. Please use different email.")
        }
        user.id = null
        user.password = passwordEncoder.encode(user.password)
        user.role = roleRepository.findByIdOrNull(1)
        userRepository.save(user)
    }

    override fun currentUser(): User {
        val email = SecurityContextHolder.getContext().authentication.name

        return userRepository.findByEmail(email)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "No user with email $email found") }
    }

    @Transactional(readOnly = true)
    override fun login(credentials: Credentials): LibraryUser {
        try {
            val usernamePassword = UsernamePasswordAuthenticationToken(credentials.username, credentials.password, listOf())
            val authentication = authenticationManager.authenticate(usernamePassword)

            val user = authentication.principal as LibraryUser
            user.token = "Bearer " + generateToken(authentication)
            
            return user
        } catch (e: AuthenticationException) {
            throw ResponseStatusException(FORBIDDEN, "username or password invalid")
        }
    }

    private fun generateToken(authentication: Authentication): String {
        val tokenLive = LocalDateTime.now().plusMinutes(expiration.toLong())
        val expiration = Date.from(tokenLive.atZone(ZoneId.systemDefault()).toInstant())
        val roles = authentication.authorities.map { it.authority }.toTypedArray()

        val claimsSet = JWTClaimsSet.Builder()
            .subject(authentication.name)
            .expirationTime(expiration)
            .claim("scope", roles)
            .build()

        val jwt = SignedJWT(header, claimsSet)
        jwt.sign(signer)
        return jwt.serialize()
    }
}
