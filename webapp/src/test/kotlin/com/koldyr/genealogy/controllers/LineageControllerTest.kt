package com.koldyr.genealogy.controllers

import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.model.EventType.Marriage
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person


var mainLineageId: Long? = null

/**
 * Description of the LineageControllerTest class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
class LineageControllerTest : BaseControllerTest() {

    @Test
    fun importExport() {
        val lineage = newLineage()

        val location = mockMvc.post("$baseUrl/import") {
            header(AUTHORIZATION, getBearerToken())
            header("Lineage-Name", lineage.name)
            header("Lineage-Note", lineage.note!!)
            content = mapper.writeValueAsString(lineage)
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status { isCreated() }
                header { string(LOCATION, Matchers.matchesRegex("$baseUrl/\\d+")) }
            }.andReturn().response.getHeader(LOCATION)
        lineage.id = getLastIdFromLocation(location)
        assertNotNull(lineage.id)

        val content = mockMvc.get("$baseUrl/${lineage.id}/export") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
//            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }
            .andReturn().response.contentAsString

        val result: Lineage = mapper.readValue(content, jacksonTypeRef())
        assertEquals(lineage.id, result.id)
        assertEquals(lineage.name, result.name)
        assertEquals(lineage.note, result.note)
        assertEquals(lineage.families.size, result.families.size)
        assertEqualsPersons(lineage.persons, result.persons)
    }

    @Test
    fun lineages() {
        val all = allLineages()
        assertTrue(all.isNotEmpty())

        mainLineageId = lineageId
        lineageId = createLineAge()

        val result = getLineage(lineageId!!)
        assertEquals(lineageId, result.id)

        result.name = "new name $lineageId"
        result.note = "new note $lineageId"

        mockMvc.put("$baseUrl/$lineageId") {
            header(AUTHORIZATION, getBearerToken())
            content = mapper.writeValueAsString(result)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }

        val updated = getLineage(lineageId!!)
        assertEquals(result.id, updated.id)
        assertEquals(result.name, updated.name)
        assertEquals(result.note, updated.note)

        val children = listOf(
            createPerson(Gender.MALE),
            createPerson(Gender.MALE),
            createPerson(Gender.FEMALE)
        ).map { it.id!! }
        val family = createFamily(children)

        //delete lineage
        mockMvc.delete("$baseUrl/$lineageId") {
            header(AUTHORIZATION, getBearerToken())
        }
            .andExpect {
                status { isNoContent() }
            }

        //check family is deleted
        mockMvc.get("$baseUrl/$lineageId/families/${family.id}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isNotFound() }
            }

        //check husband is deleted
        mockMvc.get("$baseUrl/$lineageId/persons/${family.husband}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isNotFound() }
            }

        //check husband is deleted
        mockMvc.get("$baseUrl/$lineageId/persons/${family.wife}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isNotFound() }
            }

        //check children is deleted
        mockMvc.get("$baseUrl/$lineageId/persons/${family.children!!.first()}") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isNotFound() }
            }

        //check lineage is deleted
        mockMvc.get("$baseUrl/$lineageId") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isNotFound() }
            }

        lineageId = mainLineageId
    }

    private fun allLineages(): List<LineageDTO> {
        val content = mockMvc.get(baseUrl) {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            //            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(APPLICATION_JSON) }
            }
            .andReturn().response.contentAsString

        return mapper.readValue(content, jacksonTypeRef())
    }

    private fun getLineage(id: Long): LineageDTO {
        val responseContent = mockMvc.get("$baseUrl/$id") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
            .andReturn().response.contentAsString

        return mapper.readValue(responseContent, LineageDTO::class.java)
    }

    private fun newLineage(): Lineage {
        val personIds = LongArray(100) { i -> 1000 + i.toLong() }.iterator()
        val familyIds = LongArray(100) { i -> 2000 + i.toLong() }.iterator()

        val husband = createPersonModel(Gender.MALE, personIds.next())
        val wife = createPersonModel(Gender.FEMALE, personIds.next())
        val children1 = listOf(
            createPersonModel(Gender.FEMALE, personIds.next()),
            createPersonModel(Gender.MALE, personIds.next())
        )
        val family1 = newFamily(familyIds, wife, husband, children1)

        val family2 = newFamily(familyIds,
            children1.get(0),
            createPersonModel(Gender.MALE, personIds.next()),
            listOf(
                createPersonModel(Gender.FEMALE, personIds.next()),
                createPersonModel(Gender.MALE, personIds.next())
            )
        )

        val family3 = newFamily(familyIds,
            createPersonModel(Gender.FEMALE, personIds.next()),
            children1.get(1),
            listOf(
                createPersonModel(Gender.FEMALE, personIds.next()),
                createPersonModel(Gender.MALE, personIds.next())
            )
        )

        val families = setOf(family1, family2, family3)
        val persons = families
            .map {
                val family = mutableSetOf(it.wife, it.husband)
                family.addAll(it.children)
                family
            }
            .flatten()
            .filterNotNull()
            .toSet()

        val lineage = Lineage(persons, families, true)
        lineage.name = "Test Lineage"
        lineage.note = "Test Lineage"
        return lineage
    }

    private fun newFamily(familyIds: LongIterator, wife: Person, husband: Person, children: List<Person>): Family {
        val family = Family(familyIds.next())
        family.wife = wife
        family.husband = husband
        family.children.addAll(children)
        family.events.add(newLifeEvent(Marriage, 2020).toFamilyEvent())

        wife.familyId = family.id
        husband.familyId = family.id
        children.forEach { it.parentFamilyId = family.id }

        return family
    }

    private fun assertEqualsPersons(persons1: Set<Person>, persons2: Set<Person>) {
        persons1.forEach { person1 ->
            val person2 = persons2.first { person2 -> person2.name == person1.name }
            assertEqualsPerson(person1, person2)
        }
    }

    private fun assertEqualsPerson(person1: Person, person2: Person) {
        assertEquals(person1.gender, person2.gender)
        assertEquals(person1.note, person2.note)
        assertEquals(person1.place, person2.place)
        assertEquals(person1.occupation, person2.occupation)
        assertEquals(person1.events, person2.events.onEach { it.id = null })
        assertEquals(person1.photo, person2.photo)
        assertEquals(person1.photoUrl, person2.photoUrl)
    }
}
