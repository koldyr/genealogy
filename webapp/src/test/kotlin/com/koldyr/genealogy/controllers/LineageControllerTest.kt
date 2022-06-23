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
        val id = all[0].id!!

        val result = getLineage(id)
        assertEquals(id, result.id)

        result.name = "new name $id"
        result.note = "new note $id"

        mockMvc.put("$baseUrl/$id") {
            header(AUTHORIZATION, getBearerToken())
            content = mapper.writeValueAsString(result)
            accept = APPLICATION_JSON
            contentType = APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }

        val updated = getLineage(id)
        assertEquals(result.id, updated.id)
        assertEquals(result.name, updated.name)
        assertEquals(result.note, updated.note)


        mockMvc.delete("$baseUrl/$id") {
            header(AUTHORIZATION, getBearerToken())
        }
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("$baseUrl/$id") {
            header(AUTHORIZATION, getBearerToken())
            accept = APPLICATION_JSON
        }
            .andExpect {
                status { isNotFound() }
            }
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