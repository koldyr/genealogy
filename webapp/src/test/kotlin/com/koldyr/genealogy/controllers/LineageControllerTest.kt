package com.koldyr.genealogy.controllers

import org.junit.Assert.*
import org.junit.Test
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.model.Gender


var mainLineageId: Long? = null

/**
 * Description of the LineageControllerTest class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
class LineageControllerTest : BaseControllerTest() {

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
        mockMvc.get("$baseUrl/$lineageId/persons/${family.children!!.toList()[0]}") {
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
}