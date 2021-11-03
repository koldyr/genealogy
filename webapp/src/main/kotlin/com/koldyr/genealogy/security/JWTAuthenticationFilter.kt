package com.koldyr.genealogy.security

import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthenticationFilter(secret: String) : UsernamePasswordAuthenticationFilter() {

    @Value("\${security.token.exp}")
    private lateinit var expiration: String

    @Autowired
    private lateinit var mapper: ObjectMapper

    private val algorithm: Algorithm
    
    init {
        algorithm = Algorithm.HMAC512(secret.toByteArray())
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        return try {
            val credentials: com.koldyr.genealogy.model.Credentials = mapper
                .readValue(request.inputStream, com.koldyr.genealogy.model.Credentials::class.java)
            val authenticationToken = UsernamePasswordAuthenticationToken(credentials.username, credentials.password, listOf())
            authenticationManager.authenticate(authenticationToken)
        } catch (e: IOException) {
            throw AuthenticationServiceException(e.message, e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, auth: Authentication) {
        val username = (auth.principal as User).username
        val tokenLive = LocalDateTime.now().plusDays(expiration.toLong())
        val token: String = JWT.create()
            .withSubject(username)
            .withExpiresAt(Date.from(tokenLive.atZone(ZoneId.systemDefault()).toInstant()))
            .sign(algorithm)
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $token")
    }
}