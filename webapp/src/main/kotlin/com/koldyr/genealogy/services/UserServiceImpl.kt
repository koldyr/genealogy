package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import javax.persistence.EntityNotFoundException

open class UserServiceImpl(
        private val userRepository: UserRepository,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder) : UserService {

    override fun createUser(userCred: User) {
        val user : Optional<User>? = userCred.email?.let { userRepository.findByEmail(it) }
        if (user?.isPresent == true) {
            throw RuntimeException("User already registered. Please use different email.")
        }

        userCred.password = bCryptPasswordEncoder.encode(userCred.password)
        userRepository.save(userCred)
    }

    override fun readUserByEmail(email: String): User {
        return userRepository.findByEmail(email).orElseThrow{ EntityNotFoundException() }
    }

}