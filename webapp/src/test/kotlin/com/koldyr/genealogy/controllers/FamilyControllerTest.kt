package com.koldyr.genealogy.controllers

import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Gender

class FamilyControllerTest : BaseControllerTest() {

    @Test
    fun families() {
        mockMvc.get("$baseUrl/$lineageId/families") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
            }

        val familyDto = createFamily(listOf())

        mockMvc.get("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(mapper.writeValueAsString(familyDto))
                }
            }

        familyDto.wife = createPerson(Gender.FEMALE).id

        mockMvc.put("$baseUrl/$lineageId/families/${familyDto.id}") {
            content = mapper.writeValueAsString(familyDto)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
            }

        mockMvc.get("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(mapper.writeValueAsString(familyDto))
                }
            }

        familyDto.wife = createPerson(Gender.MALE).id
        mockMvc.put("$baseUrl/$lineageId/families/${familyDto.id}") {
            content = mapper.writeValueAsString(familyDto)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("Person with id '${familyDto.wife}' is man and can not be wife")
                }
            }

        mockMvc.delete("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isNotFound()
                    reason("Family with id '${familyDto.id}' is not found")
                }
            }

        mockMvc.post("$baseUrl/$lineageId/families") {
            content = mapper.writeValueAsString(FamilyDTO())
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("husband or wife must be is not empty")
                }
            }
    }

    @Test
    fun deleteFamily() {
        val children = listOf(
            createPerson(Gender.FEMALE).id!!,
            createPerson(Gender.FEMALE).id!!
        )

        val familyDto = createFamily(children)

        mockMvc.get("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(APPLICATION_JSON)
                    json(mapper.writeValueAsString(familyDto))
                }
            }

        mockMvc.delete("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$baseUrl/$lineageId/families/${familyDto.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isNotFound()
                    reason("Family with id '${familyDto.id}' is not found")
                }
            }
    }

    @Test
    fun events() {
        val randomId: Int = (99999..999999).random()
        mockMvc.get("$baseUrl/$lineageId/families/$randomId/events") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isNotFound()
                    reason("Family with id '${randomId}' is not found")
                }
            }

        val familyDTO = createFamily(listOf())
        val familyEvent = createFamilyEventModel()
        val location = mockMvc.post("$baseUrl/$lineageId/families/${familyDTO.id}/events") {
            content = mapper.writeValueAsString(familyEvent)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { exists(LOCATION) }
                header { string(LOCATION, Matchers.matchesRegex("$baseUrl/$lineageId/families/[\\d]+/events/[\\d]+")) }
            }.andReturn().response.getHeader(LOCATION)
        familyEvent.id = getLastIdFromLocation(location)

        mockMvc.get("$baseUrl/1/families/${familyDTO.id}/events") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { json(mapper.writeValueAsString(listOf(familyEvent))) }
            }

        mockMvc.delete("$baseUrl/$lineageId/families/${familyDTO.id}/events/${familyEvent.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$baseUrl/$lineageId/families/${familyDTO.id}/events") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { json("[]") }
            }
    }

    @Test
    fun children() {
        val randomId: Int = (99999..999999).random()
        mockMvc.get("$baseUrl/$lineageId/families/$randomId/children") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isNotFound()
                    reason("Family with id '${randomId}' is not found")
                }
            }

        val familyDTO = createFamily(listOf())

        val child1 = createPersonModel(Gender.MALE)
        val childId1 = mockMvc.post("$baseUrl/$lineageId/families/${familyDTO.id}/children") {
            content = mapper.writeValueAsString(child1)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { string(LOCATION, Matchers.matchesRegex("$baseUrl/$lineageId/persons/[\\d]+")) }
            }.andReturn().response.getHeader(LOCATION)
        child1.id = getLastIdFromLocation(childId1!!)
        child1.parentFamilyId = familyDTO.id

        val child2 = createPerson(Gender.FEMALE)
        val childId2 = mockMvc.patch("$baseUrl/$lineageId/families/${familyDTO.id}/children/${child2.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { string(LOCATION, Matchers.matchesRegex("$baseUrl/$lineageId/persons/[\\d]+")) }
            }.andReturn().response.getHeader(LOCATION)
        child2.id = getLastIdFromLocation(childId2!!)
        child2.parentFamilyId = familyDTO.id

        mockMvc.get("$baseUrl/$lineageId/families/${familyDTO.id}/children") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { json(mapper.writeValueAsString(listOf(child1, child2))) }
            }

        mockMvc.delete("$baseUrl/$lineageId/families/${familyDTO.id}/children/${child1.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.delete("$baseUrl/$lineageId/families/${familyDTO.id}/children/${child1.id}") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status {
                    isBadRequest()
                    reason("Child with id '${child1.id}' is not found in family")
                }
            }

        mockMvc.get("$baseUrl/$lineageId/families/${familyDTO.id}/children") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { json(mapper.writeValueAsString(listOf(child2))) }
            }
    }
}
