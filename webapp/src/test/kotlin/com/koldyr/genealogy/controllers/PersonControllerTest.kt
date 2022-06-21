package com.koldyr.genealogy.controllers

import java.time.LocalDate
import java.time.Month
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.hamcrest.Matchers
import org.junit.Test
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.genealogy.dto.PageResultDTO
import com.koldyr.genealogy.dto.SearchDTO
import com.koldyr.genealogy.dto.SearchEventDTO
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.PersonNames

/**
 * Description of class PersonControllerTest
 *
 * @author d.halitski@gmail.com
 * @created: 2021-10-23
 */
class PersonControllerTest : BaseControllerTest() {

    private val pageResultType = jacksonTypeRef<PageResultDTO<Person>>()

    private fun getUpdatePersonModel(person: Person): Person {
        person.name!!.first = createRandomWord()
        person.name!!.last = createRandomWord()
        person.name!!.middle = createRandomWord()
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
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(personModel)) }
            }

        val personEntityChange = getUpdatePersonModel(personModel)
        mockMvc.put("/api/genealogy/persons/${personModel.id}") {
            header(AUTHORIZATION, getBearerToken())
            content = mapper.writeValueAsString(personEntityChange)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }

        mockMvc.get("/api/genealogy/persons/${personModel.id}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(personEntityChange)) }
            }

        mockMvc.delete("/api/genealogy/persons/${personModel.id}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("/api/genealogy/persons/${personModel.id}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                status { reason("Person with id '${personModel.id}' is not found") }
            }
    }

    @Test
    fun events() {
        val randomId: Int = (99999..999999).random()
        mockMvc.get("/api/genealogy/persons/$randomId/events") {
            accept = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                status { reason("Person with id '$randomId' is not found") }
            }

        val person = createPerson(Gender.FEMALE)
        mockMvc.get("/api/genealogy/persons/${person.id}/events") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }

        val personEvent = createPersonEventModel()
        val personEventId = mockMvc.post("/api/genealogy/persons/${person.id}/events") {
            content = mapper.writeValueAsString(personEvent)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { exists(LOCATION) }
                header { string(LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+/events/[\\d]+")) }
            }.andReturn().response.getHeader(LOCATION)
        personEvent.id = getLastIdFromLocation(personEventId)

        mockMvc.get("/api/genealogy/persons/${person.id}/events") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
                content { json(mapper.writeValueAsString(listOf(personEvent))) }
            }

        mockMvc.delete("/api/genealogy/persons/${person.id}/events/${personEvent.id}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNoContent() }

            }

        mockMvc.delete("/api/genealogy/persons/${person.id}/events/${personEvent.id}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                status { reason("Event with id '${personEvent.id}' is not found") }
            }
    }

    @Test
    fun search() {
        for (i in (1..10)) {
            createPerson(newPerson(i))
        }

        var criteria = SearchDTO().apply { name = "first-1" }

        var result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 2)

        criteria = SearchDTO(name = "middle-2")
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 1)

        criteria = SearchDTO(name = "last-3")
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 1)

        criteria = SearchDTO(note = "note-4")
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 1)

        criteria = SearchDTO(name = "maiden-5")
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 1)

        criteria = SearchDTO(place = "place-6")
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 1)

        criteria = SearchDTO(occupation = "occupation-7")
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 1)

        criteria = SearchDTO(event = SearchEventDTO(
            type = EventType.Birth.getCode(),
            dateFrom = LocalDate.of(1993, Month.JANUARY, 1),
            dateTo = LocalDate.of(1996, Month.JANUARY, 1)
        ))
        result = searchPerson(criteria)
        assertNotNull(result)
        assertTrue(result.size == 4)
    }

    private fun searchPerson(criteria: SearchDTO): List<Person> {
        val response = mockMvc.post("/api/genealogy/persons/search") {
            content = mapper.writeValueAsString(criteria)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
            }.andReturn().response

        val result = mapper.readValue(response.contentAsString, pageResultType)
        return result.result
    }

    private fun newPerson(i: Int): Person {
        val person = Person()
        person.name = PersonNames("first-$i", "middle-$i", "last-$i", if (i % 2 == 0) null else "maiden-$i")
        person.gender = if (i % 2 == 0) Gender.MALE else Gender.FEMALE
        person.place = "place-$i"
        person.note = "note-$i"
        person.occupation = "occupation-$i"
        person.events.add(
            PersonEvent(EventType.Birth, null, LocalDate.of(1990 + i, Month.JANUARY, 1), "place-$i", "note-$i")
        )
        return person
    }
}
