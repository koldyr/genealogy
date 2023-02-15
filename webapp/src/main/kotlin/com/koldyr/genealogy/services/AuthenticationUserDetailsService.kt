package com.koldyr.genealogy.services

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import com.koldyr.genealogy.dto.LibraryUser
import com.koldyr.genealogy.persistence.UserRepository

/**
 * Description of class AuthenticationUserDetailsService
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
class AuthenticationUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails = userRepository
        .findByEmail(email)
        .map { LibraryUser(it) }
        .orElseThrow { UsernameNotFoundException("Wrong username: $email") }
}
