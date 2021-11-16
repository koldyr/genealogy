package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.security.UnSecured
import com.koldyr.genealogy.services.UserService
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/user")
@UnSecured
class UserController(private val userService: UserService) {

    @PostMapping("/registration")
    fun create(@RequestBody user: User): ResponseEntity<Unit> {
        userService.createUser(user)

        val uri = URI.create("/api/user/login")
        return ResponseEntity.created(uri).build()
    }

    @PostMapping("/login")
    fun login(@RequestBody credentials: Credentials): ResponseEntity<Unit> {
        val login = userService.login(credentials)
        return ResponseEntity.ok().header(AUTHORIZATION, login).build()
    }
}