package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.Genealogy
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

/**
 * Description of class PersonControllerTest
 * @created: 2021-10-23
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Genealogy::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
@IfProfileValue(name = "spring.profiles.active", values = ["int-test"])
class PersonControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun persons() {
        mockMvc.get("/api/genealogy/persons") {
            accept = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("[]") }
            }
    }

    @Test
    fun events() {
        mockMvc.get("/api/genealogy/persons/1/events") {
            accept = MediaType.APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("[]") }
            }
    }
}