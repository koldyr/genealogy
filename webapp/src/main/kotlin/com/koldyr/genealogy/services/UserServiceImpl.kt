package com.koldyr.genealogy.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import javax.persistence.EntityNotFoundException

open class UserServiceImpl(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        secret: String) : UserService {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Value("\${security.token.exp}")
    private lateinit var expiration: String

    private val algorithm: Algorithm = Algorithm.HMAC512(secret.toByteArray())

    override fun createUser(userModel: User) {
        if (userModel.email.isBlank() || userModel.password.isBlank() || userModel.name.isBlank() || userModel.surName.isBlank()) {
            throw ResponseStatusException(BAD_REQUEST, "invalid data")
        }
        if (userRepository.findByEmail(userModel.email).isPresent) {
            throw ResponseStatusException(BAD_REQUEST, "User already registered. Please use different email.")
        }
        userModel.id = null
        userModel.password = passwordEncoder.encode(userModel.password)
        userRepository.save(userModel)
    }
    
    override fun currentUser(): User {
        val username: String = SecurityContextHolder.getContext().authentication.principal.toString()
        return userRepository.findByEmail(username)
                .orElseThrow { EntityNotFoundException() }
    }

    override fun login(credentials: Credentials): String {
        try {
            val token = UsernamePasswordAuthenticationToken(credentials.username, credentials.password, listOf())
            authenticationManager.authenticate(token)
            
            return "Bearer " + generateToken(credentials.username)
        } catch (e: DisabledException) {
            throw ResponseStatusException(UNAUTHORIZED, "user is disabled")
        } catch (e: BadCredentialsException) {
            throw ResponseStatusException(UNAUTHORIZED, "username or password invalid")
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
