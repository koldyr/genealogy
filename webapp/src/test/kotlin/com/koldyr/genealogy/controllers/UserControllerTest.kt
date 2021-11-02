package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.context.ContextLoadTest
import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.Gender
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class UserControllerTest : ContextLoadTest() {

    @Test
    fun registrationUser() {
        mockMvc.post("/api/user/registration") {
            content = mapper.writeValueAsString(createUser())
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("User already registered. Please use different email.") }
                }
    }

    @Test
    fun authenticationUser() {
        val creds = Credentials()
        creds.username = "yan@gmail.com"
        creds.password = "1112"

        mockMvc.post("/api/user/login") {
            content = mapper.writeValueAsString(creds)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("username or password invalid") }
                }
    }

    @Test
    fun authorizationUser() {
        val person = createPerson(Gender.MALE)
        mockMvc.get("/api/genealogy/persons/${person.id}") {
            header(HttpHeaders.AUTHORIZATION, "Bearer fjdkslfjdslkjjlkdsfj")
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isUnauthorized() }
                    status { reason("invalid token") }
                }

        mockMvc.get("/api/genealogy/persons/${person.id}") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isForbidden() }
                    status { reason("Access Denied") }
                }
    }
}