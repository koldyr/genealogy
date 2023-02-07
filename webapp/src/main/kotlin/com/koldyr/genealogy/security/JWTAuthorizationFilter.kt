package com.koldyr.genealogy.security

import java.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier

private const val PREFIX_BEARER = "Bearer "
private const val PATH_USER = "user"

open class JWTAuthorizationFilter(
    secret: String,
    authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService
) : BasicAuthenticationFilter(authenticationManager) {

    private val jwtVerifier: JWTVerifier
    
    init {
        val algorithm = Algorithm.HMAC512(secret.toByteArray())
        jwtVerifier = JWT.require(algorithm).build()
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
            response.sendError(FORBIDDEN.value(), "Invalid token: ${ex.message}")
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
        val jwt = jwtVerifier.verify(token.replace(PREFIX_BEARER, ""))

        val user = userDetailsService.loadUserByUsername(jwt.subject)
        return authenticated(user.username, user, user.authorities)
    }
}
