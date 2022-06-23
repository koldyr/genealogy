package com.koldyr.genealogy.security

import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException

private const val PREFIX_BEARER = "Bearer "
private const val PATH_USER = "user"

open class JWTAuthorizationFilter(
    secret: String,
    authenticationManager: AuthenticationManager
) : BasicAuthenticationFilter(authenticationManager) {

    private val algorithm: Algorithm

    init {
        algorithm = Algorithm.HMAC512(secret.toByteArray())
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            if (isAnonymous(request)) {
                chain.doFilter(request, response)
                return
            }
            val header = request.getHeader(AUTHORIZATION)
            val authentication = getAuthentication(header)
            SecurityContextHolder.getContext().authentication = authentication

            chain.doFilter(request, response)
        } catch (ex: JWTVerificationException) {
            response.sendError(FORBIDDEN.value(), "invalid token")
        }
    }

    private fun isAnonymous(request: HttpServletRequest): Boolean {
        if (request.requestURI.contains(PATH_USER)) {
            return true
        }

        val header = request.getHeader(AUTHORIZATION)
        return header == null || !header.startsWith(PREFIX_BEARER)
    }

    private fun getAuthentication(token: String): Authentication {
        val user = JWT.require(algorithm)
                .build()
                .verify(token.replace(PREFIX_BEARER, ""))
                .subject
        return UsernamePasswordAuthenticationToken(user, null, listOf())
    }
}
