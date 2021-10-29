package com.koldyr.genealogy.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.koldyr.genealogy.security.AuthenticationConfigConstant.HEADER_STRING
import com.koldyr.genealogy.security.AuthenticationConfigConstant.TOKEN_PREFIX
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthenticationFilter() : UsernamePasswordAuthenticationFilter() {


    @Value("\${security.secret}")
    private lateinit var secret: String

    @Value("\${security.token.exp}")
    private lateinit var expiration: String


    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        return try {
            val creds: com.koldyr.genealogy.model.User = ObjectMapper()
                    .readValue(request.inputStream, com.koldyr.genealogy.model.User::class.java)
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            creds.email,
                            creds.password,
                            ArrayList())
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, auth: Authentication) {
        val token: String = JWT.create()
                .withSubject((auth.getPrincipal() as User).getUsername())
                .withExpiresAt(Date(System.currentTimeMillis() + expiration.toInt()))
                .sign(Algorithm.HMAC512(secret.toByteArray()))
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token)
    }
}