package com.koldyr.genealogy.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.koldyr.genealogy.Genealogy
import com.koldyr.genealogy.model.*
import com.koldyr.genealogy.persistence.PersonRepository
import org.hamcrest.Matchers.matchesRegex
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.*
import java.time.LocalDate

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

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    private fun createPersonModel(): Person {
        val person = Person()
        person.name = PersonNames()
        person.name?.first = "p5_name"
        person.name?.middle = "p5_middle"
        person.name?.last = "p5_last"
        person.gender = Gender.MALE
        person.place = "p5_place"
        person.occupation = "p5_occup"
        person.note = "p5_note"

        return person
    }

    private fun getUpdatePersonModel(person: Person): Person {
        person.name?.first = "p6_name"
        person.name?.last = "p6_last"
        person.name?.middle = "p6_middle"
        return person
    }

    private fun getIdFromLocation(location: String): Int {
        val id = location.subSequence(location.lastIndexOf("/") + 1, location.length)
        return Integer.parseInt(id as String?)
    }

    private fun createPerson(personModel: Person): Int {
        val location = mockMvc.post("/api/genealogy/persons") {
            content = mapper.writeValueAsString(personModel)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(LOCATION) }
                    header { string(LOCATION, matchesRegex("/api/genealogy/persons/[\\d]+")) }
                }.andReturn().response.getHeader(LOCATION)
        return getIdFromLocation(location)
    }

    private fun createEvent(personEvent: PersonEvent, id: Int): Int {
        val location = mockMvc.post("/api/genealogy/persons/$id/events") {
            content = mapper.writeValueAsString(personEvent)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(LOCATION) }
                    header { string(LOCATION, matchesRegex("/api/genealogy/persons/[\\d]+/events/[\\d]+")) }
                }.andReturn().response.getHeader(LOCATION)
        return getIdFromLocation(location)

    }

    @Test
    fun persons() {
        mockMvc.get("/api/genealogy/persons") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }

        val personModel = createPersonModel()
        val id: Int = createPerson(personModel)
        personModel.id = id;
        mockMvc.get("/api/genealogy/persons/$id") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(personModel)) }
                }

        val personEntityChange = getUpdatePersonModel(personModel)
        mockMvc.put("/api/genealogy/persons/$id") {
            content = mapper.writeValueAsString(personEntityChange)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                }

        mockMvc.delete("/api/genealogy/persons/$id") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }
                }

        mockMvc.get("/api/genealogy/persons/$id") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }

    }

    @Test
    fun events() {
        mockMvc.get("/api/genealogy/persons/1/events") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }
                }

        val personId: Int = createPerson(createPersonModel())
        mockMvc.get("/api/genealogy/persons/$personId/events") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }

        val personEvent = PersonEvent(EventType.Birth, EventPrefix.About, LocalDate.now(),
                "Minsk", "Minsk")
        val eventId = createEvent(personEvent, personId)
        personEvent.id = eventId

        mockMvc.get("/api/genealogy/persons/$personId/events") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    content { json(mapper.writeValueAsString(mutableListOf(personEvent))) }
                }

        mockMvc.delete("/api/genealogy/persons/$personId/events/$eventId") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNoContent() }

                }

        mockMvc.delete("/api/genealogy/persons/$personId/events/$eventId") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isNotFound() }

                }

    }
}