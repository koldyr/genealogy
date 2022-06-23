package com.koldyr.genealogy.controllers

import org.junit.Test
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.Gender

class UserControllerTest : BaseControllerTest() {

    @Test
    fun registerExistingUser() {
        mockMvc.post("/api/user/registration") {
            content = mapper.writeValueAsString(createUser())
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                status { reason("User already registered. Please use different email.") }
            }
    }

    @Test
    fun registerUserNoPassword() {
        val credentials = Credentials().apply {
            username = "lemming@koldyr.com"
        }
        mockMvc.post("/api/user/registration") {
            content = mapper.writeValueAsString(credentials)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
                status { reason("invalid data") }
            }
    }

    @Test
    fun wrongPassword() {
        val credentials = Credentials().apply {
            username = "me@koldyr.com"
            password = "1112"
        }

        mockMvc.post("/api/user/login") {
            content = mapper.writeValueAsString(credentials)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("username or password invalid") }
            }
    }

    @Test
    fun wrongUser() {
        val credentials = Credentials().apply {
            username = "you@koldyr.com"
            password = "koldyr"
        }

        mockMvc.post("/api/user/login") {
            content = mapper.writeValueAsString(credentials)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("username or password invalid") }
            }
    }

    @Test
    fun wrongToken() {
        val person = createPerson(Gender.MALE)
        mockMvc.get("$baseUrl/$lineageId/persons/${person.id}") {
            header(AUTHORIZATION, "Bearer 12345")
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("invalid token") }
            }

        mockMvc.get("$baseUrl/$lineageId/persons/${person.id}") {
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("Access Denied") }
            }
    }

    @Test
    fun noToken() {
        val person = createPerson(Gender.MALE)

        mockMvc.get("$baseUrl/$lineageId/persons/${person.id}") {
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("Access Denied") }
            }
    }
}
