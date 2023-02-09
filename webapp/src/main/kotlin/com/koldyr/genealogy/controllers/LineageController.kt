package com.koldyr.genealogy.controllers

import java.net.URI
import org.springframework.http.HttpHeaders.*
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.services.LineageService

const val TEXT_CSV = "text/csv"
const val TEXT_GED = "text/ged"

/**
 * Description of the LineageController class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
@RestController
@RequestMapping("/api/lineage")
@Tags(value = [Tag(name = "LineageController")])
class LineageController(private val lineageService: LineageService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun lineages(): Collection<LineageDTO> = lineageService.findAll()

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody lineage: LineageDTO): ResponseEntity<Unit> {
        val lineageId = lineageService.create(lineage)

        val uri = URI.create("/api/lineage/${lineageId}")
        return created(uri).build()
    }

    @GetMapping("/{lineageId}", produces = [APPLICATION_JSON_VALUE])
    fun lineageById(@PathVariable lineageId: Long): LineageDTO = lineageService.findById(lineageId)

    @PutMapping("/{lineageId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable lineageId: Long, @RequestBody lineage: LineageDTO): ResponseEntity<Unit> {
        lineageService.update(lineageId, lineage)

        return ok().build()
    }

    @DeleteMapping("/{lineageId}")
    fun delete(@PathVariable lineageId: Long): ResponseEntity<Unit> {
        lineageService.delete(lineageId)

        return noContent().build()
    }

    @PostMapping("/import", consumes = [APPLICATION_JSON_VALUE, TEXT_CSV, TEXT_GED])
    fun importLineage(
        @RequestHeader(CONTENT_TYPE) dataType: String,
        @RequestHeader("Lineage-Name") name: String,
        @RequestHeader("Lineage-Note", required = false) note: String?,
        @RequestBody lineage: ByteArray
    ): ResponseEntity<Unit> {
        val lineageId = lineageService.importLineage(dataType, lineage, name, note)

        val uri = URI.create("/api/lineage/${lineageId}")
        return created(uri).build()
    }

    @GetMapping("/{lineageId}/export", produces = [APPLICATION_JSON_VALUE, TEXT_CSV, TEXT_GED])
    fun exportLineage(
        @PathVariable lineageId: Long,
        @RequestHeader(ACCEPT, required = false) dataType: String?,
    ): ResponseEntity<ByteArray> {
        val lineage = lineageService.exportLineage(lineageId, dataType)

        return ok(lineage)
    }
}
