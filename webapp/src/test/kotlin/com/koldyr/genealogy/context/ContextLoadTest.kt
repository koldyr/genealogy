package com.koldyr.genealogy.context

import com.fasterxml.jackson.databind.ObjectMapper
import com.koldyr.genealogy.Genealogy
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.*
import com.koldyr.genealogy.persistence.UserRepository
import org.apache.commons.lang3.RandomStringUtils
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
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

    @Autowired
    lateinit var userRepository: UserRepository

    protected fun createPersonModel(gender: Gender): Person {
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

    protected fun createUser() : User {
        val user = User()
        user.password = "1111"
        user.email = "yan@gmail.com"
        user.name = "Yan"
        user.surName = "Efimov"
        return user
    }

    @Before
    open fun loadUser () {
        val user = createUser()
        val location = mockMvc.post("/api/user/registration") {
            content = mapper.writeValueAsString(user)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    header { exists(HttpHeaders.LOCATION) }
                    header { string(HttpHeaders.LOCATION, Matchers.matchesRegex("/api/user/login")) }
                }.andReturn().response.getHeader(HttpHeaders.LOCATION)
    }

    @After
    open fun deleteUser () {
        userRepository.deleteAll()
    }

    protected fun getBearerToken() : String {
        val creds = Credentials()
        creds.username = "yan@gmail.com"
        creds.password = "1111"

        val token = mockMvc.post("/api/user/login") {
            content = mapper.writeValueAsString(creds)
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    header { exists(HttpHeaders.AUTHORIZATION) }
                }.andReturn().response.getHeader(HttpHeaders.AUTHORIZATION)
        return token
    }

    protected fun createPersonEventModel(): PersonEvent {
        return PersonEvent(EventType.Birth, EventPrefix.About, LocalDate.now(),
                createRandomWord(), createRandomWord())
    }

    protected fun createFamilyEventModel(): FamilyEvent {
        return FamilyEvent(EventType.Birth, EventPrefix.About, LocalDate.now(),
                createRandomWord(), createRandomWord());
    }

    protected fun getLastIdFromLocation(location: String): Int {
        val match = Regex("(\\d+)$").find(location)
        return Integer.parseInt(match!!.groups.last()!!.value)
    }

    protected fun createPerson(gender: Gender): Person {
        val personModel = createPersonModel(gender)
        val location = mockMvc.post("/api/genealogy/persons") {
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
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

    protected fun createSuccessFamilyModel() : FamilyDTO {
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
            header(HttpHeaders.AUTHORIZATION, getBearerToken())
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

    protected fun createRandomWord(): String {
        return RandomStringUtils.randomAlphabetic(2, 10);
    }
}
