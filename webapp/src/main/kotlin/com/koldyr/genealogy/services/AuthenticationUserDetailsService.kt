package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class AuthenticationUserDetailsService(
        private val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(p0: String?): UserDetails {
        val user : User = userService.readUserByEmail(p0!!)
        return org.springframework.security.core.userdetails.User(user.email, user.password, emptyList())
    }
}