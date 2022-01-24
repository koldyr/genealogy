package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import javax.persistence.EntityNotFoundException

class AuthenticationUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email).orElseThrow { EntityNotFoundException() }
        return org.springframework.security.core.userdetails.User(user.email, user.password, listOf())
    }
}
