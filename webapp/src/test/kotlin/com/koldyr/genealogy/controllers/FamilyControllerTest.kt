package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.context.ContextLoadTest
import com.koldyr.genealogy.model.Gender
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put

class FamilyControllerTest : ContextLoadTest() {

    @Test
    fun families() {
        mockMvc.get("/api/genealogy/families") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                    content { json("[]") }
                }

        val familyDto = createFamily()

        mockMvc.get("/api/genealogy/families/${familyDto.id}") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(familyDto)) }
                }

        familyDto.wife = createPerson(Gender.FEMALE).id

        mockMvc.put("/api/genealogy/families/${familyDto.id}") {
            content = mapper.writeValueAsString(familyDto)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }

    }

    @Test
    fun events() {
        mockMvc.get("/api/genealogy/families/${(99999..999999).random()}/events") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
    }

    @Test
    fun children() {
        mockMvc.get("/api/genealogy/families/${(99999..999999).random()}/children") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }
    }
}