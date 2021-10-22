package com.koldyr.genealogy.test.controllers

import com.koldyr.genealogy.Genealogy
import com.koldyr.genealogy.persistence.FamilyRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Genealogy::class])
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-test.yaml"])
class FamilyControllerTest {

    @Autowired
    lateinit var familyRepository: FamilyRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun families() {
        mockMvc.get("/api/genealogy/families") {
            contentType = MediaType.APPLICATION_JSON
        }
                .andExpect { status { isOk() } }
    }

    @Test
    fun events() {

    }

    @Test
    fun children() {

    }
}