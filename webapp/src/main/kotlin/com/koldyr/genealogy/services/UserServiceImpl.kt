package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.persistence.EntityNotFoundException

open class UserServiceImpl(
        private val userRepository: UserRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder) : UserService {

    override fun createUser(userModel: User) {
        if (userModel.email.isBlank() || userModel.password.isBlank() || userModel.name.isBlank() || userModel.surName.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid data")
        }
        val user : Optional<User> = userRepository.findByEmail(userModel.email)
        if (user.isPresent) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User already registered. Please use different email.")
        }

        userModel.id = null
        userModel.password = bCryptPasswordEncoder.encode(userModel.password)
        userRepository.save(userModel)
    }

    override fun readUserByEmail(email: String): User {
        return userRepository.findByEmail(email).orElseThrow{ EntityNotFoundException() }
    }

    override fun currentUser(): User {
        val username : String = SecurityContextHolder.getContext().authentication.principal.toString()
        return readUserByEmail(username)
    }

}