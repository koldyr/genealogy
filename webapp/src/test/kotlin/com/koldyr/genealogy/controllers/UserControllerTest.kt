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
    fun registrationUser() {
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
    fun authenticationUser() {
        val creds = Credentials()
        creds.username = "yan@gmail.com"
        creds.password = "1112"

        mockMvc.post("/api/user/login") {
            content = mapper.writeValueAsString(creds)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isUnauthorized() }
                status { reason("username or password invalid") }
            }
    }

    @Test
    fun authorizationUser() {
        val person = createPerson(Gender.MALE)
        mockMvc.get("/api/genealogy/persons/${person.id}") {
            header(AUTHORIZATION, "Bearer fjdkslfjdslkjjlkdsfj")
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("invalid token") }
            }

        mockMvc.get("/api/genealogy/persons/${person.id}") {
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                status { reason("Access Denied") }
            }
    }
}
