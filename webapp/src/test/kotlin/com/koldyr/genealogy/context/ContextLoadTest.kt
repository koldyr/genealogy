package com.koldyr.genealogy.context

import com.fasterxml.jackson.databind.ObjectMapper
import com.koldyr.genealogy.Genealogy
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.*
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.Matchers
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDate

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Genealogy::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["spring.config.location = classpath:application-test.yaml"])
@IfProfileValue(name = "spring.profiles.active", values = ["int-test"])
abstract class ContextLoadTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    private fun createPersonModel(gender: Gender): Person {
        val person = Person()
        person.name = PersonNames()
        person.name?.first = createRandomWord()
        person.name?.middle = createRandomWord()
        person.name?.last = createRandomWord()
        person.gender = gender
        person.place = createRandomWord()
        person.occupation = createRandomWord()
        person.note = createRandomWord()

        return person
    }

    private fun createPersonEventModel(): PersonEvent {
        return PersonEvent(EventType.Birth, EventPrefix.About, LocalDate.now(),
                createRandomWord(), createRandomWord())
    }

    private fun createFamilyEventModel(): FamilyEvent {
        return FamilyEvent(EventType.Birth, EventPrefix.About, LocalDate.now(),
                createRandomWord(), createRandomWord());
    }

    private fun getLastIdFromLocation(location: String): Int {
        val match = Regex("(\\d+)$").find(location)
        return Integer.parseInt(match!!.groups.last()!!.value)
    }

    protected fun createPerson(gender: Gender): Person {
        val personModel = createPersonModel(gender)
        val location = mockMvc.post("/api/genealogy/persons") {
            content = mapper.writeValueAsString(personModel)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        personModel.id = getLastIdFromLocation(location)
        return personModel
    }

    protected fun createPersonEvent(id: Int): PersonEvent {
        val personEvent = createPersonEventModel()
        val location = mockMvc.post("/api/genealogy/persons/$id/events") {
            content = mapper.writeValueAsString(personEvent)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+/events/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        personEvent.id = getLastIdFromLocation(location)
        return personEvent

    }

    private fun createSuccessFamilyModel() : FamilyDTO {
        val familyDTO = FamilyDTO()
        familyDTO.wife = createPerson(Gender.FEMALE).id
        familyDTO.husband = createPerson(Gender.MALE).id
        return familyDTO
    }

    protected fun createFamily(): FamilyDTO {
        val familyDTO = createSuccessFamilyModel()
        val location = mockMvc.post("/api/genealogy/families") {
            content = mapper.writeValueAsString(familyDTO)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/families/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        familyDTO.id = getLastIdFromLocation(location)
        familyDTO.children = mutableListOf()
        familyDTO.events = mutableListOf()
        return familyDTO
    }

    protected fun createFamilyEvent(familyDTO: FamilyDTO): FamilyEvent {
        val familyEvent = createFamilyEventModel()
        val location = mockMvc.post("/api/genealogy/families/${familyDTO.id}/events") {
            content = mapper.writeValueAsString(familyEvent)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/families/[\\d]+/events/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        familyEvent.id = getLastIdFromLocation(location)
        return familyEvent
    }

    protected fun createChildOnFamily(familyDTO: FamilyDTO): Person {
        val person = createPerson(Gender.MALE)
        val location = mockMvc.post("/api/genealogy/families/${familyDTO.id}/children") {
            content = mapper.writeValueAsString(person)
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        person.id = getLastIdFromLocation(location)
        return person
    }

    protected fun patchChildOnFamilyWithId(familyDTO: FamilyDTO): Person {
        val person = createPerson(Gender.MALE)
        val location = mockMvc.patch("/api/genealogy/families/${familyDTO.id}/children/${person.id}") {
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/genealogy/persons/[\\d]+")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
        person.id = getLastIdFromLocation(location)
        return person
    }

    protected fun createRandomWord(): String {
        return RandomStringUtils.randomAlphabetic(2, 10);
    }
}