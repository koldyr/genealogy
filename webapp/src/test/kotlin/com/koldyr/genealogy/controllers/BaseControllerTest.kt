package com.koldyr.genealogy.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.koldyr.genealogy.Genealogy
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.User
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonEventRepository
import com.koldyr.genealogy.persistence.PersonRepository
import com.koldyr.genealogy.persistence.UserRepository
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.hamcrest.Matchers.matchesRegex
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
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
abstract class BaseControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var familyRepository: FamilyRepository

    @Autowired
    lateinit var personEventRepository: PersonEventRepository

    @Autowired
    lateinit var familyEventRepository: PersonEventRepository

    protected fun createPersonModel(gender: Gender): Person {
        val person = Person()
        person.name = PersonNames(createRandomWord(), createRandomWord(), createRandomWord(), null)
        person.gender = gender
        person.place = createRandomWord()
        person.occupation = createRandomWord()
        person.note = createRandomWord()

        return person
    }

    protected fun createUser(): User {
        val user = User()
        user.password = "1111"
        user.email = "yan@gmail.com"
        user.name = "Yan"
        user.surName = "Efimov"
        return user
    }

    @Before
    fun loadUser() {
        val user = createUser()
        mockMvc.post("/api/user/registration") {
            content = mapper.writeValueAsString(user)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isCreated() }
                header { exists(LOCATION) }
                header { string(LOCATION, matchesRegex("/api/user/login")) }
            }
    }

    @After
    fun deleteUser() {
        personEventRepository.deleteAll()
        familyEventRepository.deleteAll()
        familyRepository.deleteAll()
        personRepository.deleteAll()
        userRepository.deleteAll()
    }

    protected fun getBearerToken(): String {
        val credentials = Credentials()
        credentials.username = "yan@gmail.com"
        credentials.password = "1111"

        val token = mockMvc.post("/api/user/login") {
            content = mapper.writeValueAsString(credentials)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                header { exists(AUTHORIZATION) }
            }.andReturn().response.getHeader(AUTHORIZATION)
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
            header(AUTHORIZATION, getBearerToken())
            content = mapper.writeValueAsString(personModel)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isCreated() }
                header { exists(LOCATION) }
                header { string(LOCATION, matchesRegex("/api/genealogy/persons/[\\d]+")) }
            }.andReturn().response.getHeader(LOCATION)
        personModel.id = getLastIdFromLocation(location)
        return personModel
    }

    protected fun createSuccessFamilyModel(): FamilyDTO {
        val familyDTO = FamilyDTO()
        familyDTO.wife = createPerson(Gender.FEMALE).id
        familyDTO.husband = createPerson(Gender.MALE).id
        return familyDTO
    }

    protected fun createFamily(): FamilyDTO {
        val familyDTO = createSuccessFamilyModel()
        val location = mockMvc.post("/api/genealogy/families") {
            content = mapper.writeValueAsString(familyDTO)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
            header(AUTHORIZATION, getBearerToken())
        }
            .andExpect {
                status { isCreated() }
                header { exists(LOCATION) }
                header { string(LOCATION, matchesRegex("/api/genealogy/families/[\\d]+")) }
            }.andReturn().response.getHeader(LOCATION)
        familyDTO.id = getLastIdFromLocation(location)
        familyDTO.children = listOf()
        familyDTO.events = listOf()
        return familyDTO
    }

    protected fun createRandomWord(): String {
        return randomAlphabetic(10);
    }
}