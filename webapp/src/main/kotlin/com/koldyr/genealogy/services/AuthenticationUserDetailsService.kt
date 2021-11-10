package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class AuthenticationUserDetailsService(
        private val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user : User = userService.readUserByEmail(email)
        return org.springframework.security.core.userdetails.User(user.email, user.password, listOf())
    }
}