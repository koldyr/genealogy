package com.koldyr.genealogy.controllers

import java.net.URI
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.koldyr.genealogy.dto.Credentials
import com.koldyr.genealogy.dto.UserDTO
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.services.UserService

@RestController
@RequestMapping("/api/v1/user")
@Tags(value = [Tag(name = "UserController")])
class UserController(
    private val userService: UserService
) : BaseController() {

    @PostMapping("/registration")
    fun create(@RequestBody @Valid user: User): ResponseEntity<Unit> {
        userService.create(user)

        val uri = URI.create("/api/v1/user/login")
        return created(uri).build()
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid credentials: Credentials): ResponseEntity<UserDTO> {
        val user = userService.login(credentials)

        return ok()
            .header(AUTHORIZATION, user.token)
            .body(UserDTO(user.userId(), user.toString()))
    }
}
