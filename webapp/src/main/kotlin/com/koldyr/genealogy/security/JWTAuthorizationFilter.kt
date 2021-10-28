package com.koldyr.genealogy.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthorizationFilter(authenticationManager: AuthenticationManager?) : BasicAuthenticationFilter(authenticationManager) {
    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response)
            return
        }
        val authentication = getAuthentication(request)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
        val token = request.getHeader("Authorization")
        if (token != null) {
            // parse the token.
            val user: String = JWT.require(Algorithm.HMAC512("Java_to_Dev_Secret".toByteArray()))
                    .build()
                    .verify(token.replace("Bearer ", ""))
                    .getSubject()
            return UsernamePasswordAuthenticationToken(user, null, ArrayList())
        }
        return null
    }
}
