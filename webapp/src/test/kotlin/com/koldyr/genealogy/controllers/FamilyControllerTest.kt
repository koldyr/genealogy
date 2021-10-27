package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.context.ContextLoadTest
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Gender
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
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

        familyDto.wife = createPerson(Gender.MALE).id
        mockMvc.put("/api/genealogy/families/${familyDto.id}") {
            content = mapper.writeValueAsString(familyDto)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("Person with id '${familyDto.wife}' is man and can not be wife") }
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

        mockMvc.post("/api/genealogy/families") {
            content = mapper.writeValueAsString(FamilyDTO())
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("husband or wife must be is not empty") }
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

        val familyDTO = createFamily()
        val familyEvent = createFamilyEvent(familyDTO)

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/events") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(mutableListOf(familyEvent))) }

                }

        mockMvc.delete("/api/genealogy/families/${familyDTO.id}/events/${familyEvent.id}") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/events") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json("[]") }

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

        val familyDTO = createFamily()
        val child1 = createChildOnFamily(familyDTO)
        val child2 = patchChildOnFamilyWithId(familyDTO)

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/children") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(mutableListOf(child1, child2))) }
                }

        mockMvc.delete("/api/genealogy/families/${familyDTO.id}/children/${child1.id}") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.delete("/api/genealogy/families/${familyDTO.id}/children/${child1.id}") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("Child with id '${child1.id}' is not found in family") }
                }

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/children") {
            accept = APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(mutableListOf(child2))) }
                }
    }
}