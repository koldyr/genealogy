package com.koldyr.genealogy.controllers

import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.containsString
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
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
        mockMvc.post("/api/v1/user/registration") {
            content = mapper.writeValueAsString(createUser())
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("User already registered. Please use different email.")
                }
            }
    }

    @Test
    fun registerUserNoPassword() {
        val credentials = Credentials().apply {
            username = "lemming@koldyr.com"
        }
        mockMvc.post("/api/v1/user/registration") {
            content = mapper.writeValueAsString(credentials)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("invalid data")
                }
            }
    }

    @Test
    fun wrongPassword() {
        val username = "me@koldyr.com"
        var password = "1112"

        mockMvc.post("/api/v1/user/login") {
            accept = APPLICATION_JSON
            headers {
                setBasicAuth(username, password, Charsets.UTF_8)
            }
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isForbidden()
                    reason("username or password invalid")
                }
            }

        password = RandomStringUtils.randomAlphabetic(260)

        mockMvc.post("/api/v1/user/login") {
            accept = APPLICATION_JSON
            headers {
                setBasicAuth(username, password, Charsets.UTF_8)
            }
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("size must be between 0 and 256")
                }
            }
    }

    @Test
    fun wrongUser() {
        val username = "you@koldyr.com"
        val password = testPassword

        mockMvc.post("/api/v1/user/login") {
            accept = APPLICATION_JSON
            headers {
                setBasicAuth(username, password, Charsets.UTF_8)
            }
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isForbidden()
                    reason("username or password invalid")
                }
            }
    }

    @Test
    fun wrongToken() {
        val person = createPerson(Gender.MALE)
        mockMvc.get("$baseUrl/$lineageId/persons/${person.id}") {
            header(AUTHORIZATION, "Bearer 12345")
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status { isUnauthorized() }
                header {
                    string(WWW_AUTHENTICATE, containsString("invalid_token"))
                }
            }

        mockMvc.get("$baseUrl/$lineageId/persons/${person.id}") {
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
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
//            .andDo { print() }
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
        newEntity.role = roleRepository.findById(0).get()
        userRepository.save(newEntity)
        
        val credentials = Credentials().apply {
            username = newUser.email
            password = newUser.password
        }
        
        val adminToken = login(credentials)

        val lineage = LineageDTO("Koldyrs", "Test lineage")
        mockMvc.post("/api/v1/lineage") {
            header(AUTHORIZATION, adminToken)
            content = mapper.writeValueAsString(lineage)
            contentType = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status { isForbidden() }
                header {
                    string(WWW_AUTHENTICATE, containsString("insufficient_scope"))
                }
            }
    }
}
