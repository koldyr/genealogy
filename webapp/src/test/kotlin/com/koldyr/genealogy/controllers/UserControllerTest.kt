package com.koldyr.genealogy.controllers

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.WWW_AUTHENTICATE
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import com.koldyr.genealogy.dto.Credentials
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.persistence.UserRepository

class UserControllerTest : BaseControllerTest() {

    @Autowired
    lateinit var userRepository: UserRepository

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
                status { isUnauthorized() }
                header {
                    string(WWW_AUTHENTICATE, containsString("invalid_token"))
                }
            }

        mockMvc.get("$baseUrl/$lineageId/persons/${person.id}") {
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isUnauthorized() }
                header {
                    string(WWW_AUTHENTICATE, `is`("Bearer"))
                }
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
                status { isUnauthorized() }
                header {
                    string(WWW_AUTHENTICATE, `is`("Bearer"))
                }
            }
    }

    @Test
    fun accessToRestrictedService() {
        val newUser = createUser("admin@koldyr.com", "second")
        newUser.name = createRandomWord()
        newUser.surName = createRandomWord()
        register(newUser)

        val newEntity = userRepository.findByEmail("admin@koldyr.com").get()
        newEntity.role = roleRepository.findByIdOrNull(0)
        userRepository.save(newEntity)
        
        val credentials = Credentials().apply {
            username = newUser.email
            password = newUser.password
        }
        
        val token = login(credentials)

        val lineage = LineageDTO("Koldyrs", "Test lineage")
        mockMvc.post("/api/lineage") {
            header(AUTHORIZATION, token)
            content = mapper.writeValueAsString(lineage)
            contentType = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                header {
                    string(WWW_AUTHENTICATE, containsString("insufficient_scope"))
                }
            }


    }
}
