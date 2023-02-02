package com.koldyr.genealogy.services

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.koldyr.genealogy.dto.AuthenticatedUser
import com.koldyr.genealogy.model.Credentials
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
open class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    @Value("\${security.secret}") secret: String
) : UserService {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Value("\${security.token.exp}")
    private lateinit var expiration: String

    private val algorithm: Algorithm = Algorithm.HMAC512(secret.toByteArray())

    @Transactional
    override fun createUser(userModel: User) {
        if (userModel.email.isBlank() || userModel.password.isBlank() || userModel.name.isBlank() || userModel.surName.isBlank()) {
            throw ResponseStatusException(BAD_REQUEST, "invalid data")
        }
        if (userRepository.findByEmail(userModel.email).isPresent) {
            throw ResponseStatusException(BAD_REQUEST, "User already registered. Please use different email.")
        }
        userModel.id = null
        userModel.password = passwordEncoder.encode(userModel.password)
        userModel.role = roleRepository.findByIdOrNull(1)
        userRepository.save(userModel)
    }

    @Transactional(readOnly = true)
    override fun currentUser(): User {
        val username: String = SecurityContextHolder.getContext().authentication.principal.toString()
        return userRepository.findByEmail(username)
            .orElseThrow { EntityNotFoundException() }
    }

    @Transactional(readOnly = true)
    override fun login(credentials: Credentials): AuthenticatedUser {
        try {
            val usernamePassword = UsernamePasswordAuthenticationToken(credentials.username, credentials.password, listOf())
            val authentication = authenticationManager.authenticate(usernamePassword)

            val authenticated = authentication.principal as AuthenticatedUser
            authenticated.token = "Bearer " + generateToken(authenticated.username)

            return authenticated
        } catch (e: BadCredentialsException) {
            throw ResponseStatusException(FORBIDDEN, "username or password invalid")
        }
    }

    private fun generateToken(username: String): String {
        val tokenLive = LocalDateTime.now().plusMinutes(expiration.toLong())
        return JWT.create()
            .withSubject(username)
            .withExpiresAt(Date.from(tokenLive.atZone(ZoneId.systemDefault()).toInstant()))
            .sign(algorithm)
    }
}
