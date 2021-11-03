package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.UserRepository
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.server.ResponseStatusException
import javax.persistence.EntityNotFoundException

open class UserServiceImpl(
        private val userRepository: UserRepository,
        private val passwordEncoder: BCryptPasswordEncoder) : UserService {

    override fun createUser(userModel: User) {
        if (userModel.email.isBlank() || userModel.password.isBlank() || userModel.name.isBlank() || userModel.surName.isBlank()) {
            throw ResponseStatusException(BAD_REQUEST, "invalid data")
        }
        userRepository.findByEmail(userModel.email)
            .orElseThrow { ResponseStatusException(BAD_REQUEST, "User already registered. Please use different email.") }

        userModel.id = null
        userModel.password = passwordEncoder.encode(userModel.password)
        userRepository.save(userModel)
    }

    override fun readUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow{ EntityNotFoundException() }
    }

    override fun currentUser(): User {
        val username : String = SecurityContextHolder.getContext().authentication.principal.toString()
        return readUserByEmail(username)
    }
}