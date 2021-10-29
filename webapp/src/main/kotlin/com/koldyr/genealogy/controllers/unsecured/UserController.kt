package com.koldyr.genealogy.controllers.unsecured

import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/user")
class UserController(private val userService: UserService) {

    @PostMapping("/registration")
    fun create(@RequestBody user : User) : ResponseEntity<Unit> {
        userService.createUser(user)
        val uri = URI.create("/api/user/login")
        return ResponseEntity.created(uri).build()
    }

    @PostMapping("/login")
    fun login(@RequestBody user: User) : ResponseEntity<User> {
        return ResponseEntity.ok().body(userService.readUserByEmail(user.email!!))
    }
}