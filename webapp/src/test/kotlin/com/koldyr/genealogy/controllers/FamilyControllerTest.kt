package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.context.ContextLoadTest
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Gender
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

class FamilyControllerTest : ContextLoadTest() {

    @Test
    fun families() {
        mockMvc.get("/api/genealogy/families") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }

        val familyDto = createFamily()

        mockMvc.get("/api/genealogy/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(familyDto)) }
                }

        familyDto.wife = createPerson(Gender.FEMALE).id

        mockMvc.put("/api/genealogy/families/${familyDto.id}") {
            content = mapper.writeValueAsString(familyDto)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }

        mockMvc.get("/api/genealogy/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(familyDto)) }
                }

        familyDto.wife = createPerson(Gender.MALE).id
        mockMvc.put("/api/genealogy/families/${familyDto.id}") {
            content = mapper.writeValueAsString(familyDto)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("Person with id '${familyDto.wife}' is man and can not be wife") }
                }

        mockMvc.delete("/api/genealogy/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.get("/api/genealogy/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Family with id '${familyDto.id}' is not found") }
                }

        mockMvc.post("/api/genealogy/families") {
            content = mapper.writeValueAsString(FamilyDTO())
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Family with id '${randomId}' is not found") }
                }

        val familyDTO = createFamily()
        val familyEvent = createFamilyEventModel()
        val location = mockMvc.post("/api/genealogy/families/${familyDTO.id}/events") {
            content = mapper.writeValueAsString(familyEvent)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(LOCATION) }
                    header { string(LOCATION, Matchers.matchesRegex("/api/genealogy/families/[\\d]+/events/[\\d]+")) }
                }.andReturn().response.getHeader(LOCATION)
        familyEvent.id = getLastIdFromLocation(location)

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/events") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(listOf(familyEvent))) }

                }

        mockMvc.delete("/api/genealogy/families/${familyDTO.id}/events/${familyEvent.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/events") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Family with id '${randomId}' is not found") }
                }

        val familyDTO = createFamily()

        val child1 = createPersonModel(Gender.MALE)
        val childId1 = mockMvc.post("/api/genealogy/families/${familyDTO.id}/children") {
            content = mapper.writeValueAsString(child1)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(LOCATION) }
                    header { string(LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+")) }
                }.andReturn().response.getHeader(LOCATION)
        child1.id = getLastIdFromLocation(childId1!!)
        child1.parentFamilyId = familyDTO.id

        val child2 = createPerson(Gender.MALE)
        val childId2 = mockMvc.patch("/api/genealogy/families/${familyDTO.id}/children/${child2.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(LOCATION) }
                    header { string(LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+")) }
                }.andReturn().response.getHeader(LOCATION)
        child2.id = getLastIdFromLocation(childId2!!)
        child2.parentFamilyId = familyDTO.id

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/children") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(listOf(child1, child2))) }
                }

        mockMvc.delete("/api/genealogy/families/${familyDTO.id}/children/${child1.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.delete("/api/genealogy/families/${familyDTO.id}/children/${child1.id}") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    status { reason("Child with id '${child1.id}' is not found in family") }
                }

        mockMvc.get("/api/genealogy/families/${familyDTO.id}/children") {
            accept = APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(listOf(child2))) }
                }
    }
}
