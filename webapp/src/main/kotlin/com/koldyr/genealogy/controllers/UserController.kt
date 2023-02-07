package com.koldyr.genealogy.controllers

import java.net.URI
import org.springframework.http.HttpHeaders.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.koldyr.genealogy.UnSecured
import com.koldyr.genealogy.dto.UserDTO
import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.services.UserService

@RestController
@RequestMapping("/api/user")
@UnSecured
class UserController(private val userService: UserService) {

    @PostMapping("/registration")
    fun create(@RequestBody user: User): ResponseEntity<Unit> {
        userService.createUser(user)

        val uri = URI.create("/api/user/login")
        return created(uri).build()
    }

    @PostMapping("/login")
    fun login(@RequestBody credentials: Credentials): ResponseEntity<UserDTO> {
        val user = userService.login(credentials)

        return ok()
            .header(AUTHORIZATION, user.token)
            .body(UserDTO(user.userId(), user.toString()))
    }
}
