package com.koldyr.genealogy.services

import javax.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import com.koldyr.genealogy.dto.AuthenticatedUser
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.UserRepository

class AuthenticationUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email).orElseThrow { EntityNotFoundException() }
        return AuthenticatedUser(user)
    }
}
