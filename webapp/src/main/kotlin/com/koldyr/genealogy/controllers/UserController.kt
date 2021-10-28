package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @PostMapping
    fun create(@RequestBody user : User) : ResponseEntity<Unit> {
        userService.createUser(user)
        return ResponseEntity.ok().build()
    }
}