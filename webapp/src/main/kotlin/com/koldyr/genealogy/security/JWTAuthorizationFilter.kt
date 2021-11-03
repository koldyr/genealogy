package com.koldyr.genealogy.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

open class JWTAuthorizationFilter(authenticationManager: AuthenticationManager) : BasicAuthenticationFilter(authenticationManager) {

    @Value("\${security.secret}")
    private lateinit var secret: String

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val header = request.getHeader(HttpHeaders.AUTHORIZATION)
            if (header == null || !header.startsWith("Bearer ")) {
                chain.doFilter(request, response)
                return
            }
            val authentication = getAuthentication(request)
            SecurityContextHolder.getContext().authentication = authentication
            chain.doFilter(request, response)

        } catch (ex: JWTVerificationException) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "invalid token")
        }
    }


    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (token != null) {
            val user: String = JWT.require(Algorithm.HMAC512(secret.toByteArray()))
                    .build()
                    .verify(token.replace("Bearer ", ""))
                    .subject
            return UsernamePasswordAuthenticationToken(user, null, listOf())
        }
        return null
    }
}
