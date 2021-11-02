package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.context.ContextLoadTest
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

/**
 * Description of class PersonControllerTest
 * @created: 2021-10-23
 */
class PersonControllerTest : ContextLoadTest() {

    private fun getUpdatePersonModel(person: Person): Person {
        person.name?.first = createRandomWord()
        person.name?.last = createRandomWord()
        person.name?.middle = createRandomWord()
        person.gender = Gender.FEMALE
        person.place = createRandomWord()
        person.occupation = createRandomWord()
        person.note = createRandomWord()
        return person
    }

    @Test
    fun persons() {
        val personModel = createPerson(Gender.MALE)

        mockMvc.get("/api/genealogy/persons/${personModel.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(personModel)) }
                }

        val personEntityChange = getUpdatePersonModel(personModel)
        mockMvc.put("/api/genealogy/persons/${personModel.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            content = mapper.writeValueAsString(personEntityChange)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }

        mockMvc.get("/api/genealogy/persons/${personModel.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(personEntityChange)) }
                }

        mockMvc.delete("/api/genealogy/persons/${personModel.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.get("/api/genealogy/persons/${personModel.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Person with id '${personModel.id}' is not found") }
                }
    }

    @Test
    fun events() {
        val randomId :Int = (99999..999999).random()
        mockMvc.get("/api/genealogy/persons/$randomId/events") {
            accept = MediaType.APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Person with id '$randomId' is not found") }
                }

        val person = createPerson(Gender.FEMALE)
        mockMvc.get("/api/genealogy/persons/${person.id}/events") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }

        val personEvent = createPersonEventModel()
        val personEventId = mockMvc.post("/api/genealogy/persons/${person.id}/events") {
            content = mapper.writeValueAsString(personEvent)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+/events/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        personEvent.id = getLastIdFromLocation(personEventId)

        mockMvc.get("/api/genealogy/persons/${person.id}/events") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(mutableListOf(personEvent))) }
                }

        mockMvc.delete("/api/genealogy/persons/${person.id}/events/${personEvent.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }

                }

        mockMvc.delete("/api/genealogy/persons/${person.id}/events/${personEvent.id}") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                    status { reason("Event with id '${personEvent.id}' is not found") }
                }
    }
}
