package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.context.ContextLoadTest
import com.koldyr.genealogy.model.Gender
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
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

        mockMvc.get("/api/genealogy/families/${familyDto.id}") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(familyDto)) }
                }

        mockMvc.delete("/api/genealogy/families/${familyDto.id}") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.get("/api/genealogy/families/${familyDto.id}") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Family with id '${familyDto.id}' is not found") }
                }

    }

    @Test
    fun events() {
        val randomId :Int = (99999..999999).random()
        mockMvc.get("/api/genealogy/families/$randomId/events") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Family with id '${randomId}' is not found") }
                }
    }

    @Test
    fun children() {
        val randomId :Int = (99999..999999).random()
        mockMvc.get("/api/genealogy/families/$randomId/children") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Family with id '${randomId}' is not found") }
                }
    }
}