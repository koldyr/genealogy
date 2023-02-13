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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import com.koldyr.genealogy.dto.ErrorResponse
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
@ApiResponse(
    description = "Internal server error",
    responseCode = "500",
    content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))]
)
class LineageController(private val lineageService: LineageService) {

    @Operation(
        description = "List of all lineages",
        responses = [
            ApiResponse(
                description = "List of lineages", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = LineageDTO::class)))
                ]
            )
        ]
    )
    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun lineages(): Collection<LineageDTO> = lineageService.findAll()

    @Operation(
        description = "Creates new lineage",
        responses = [ApiResponse(
            description = "Lineage is created",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = LOCATION, description = "url to new lineage")]
        )]
    )
    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody lineage: LineageDTO): ResponseEntity<Unit> {
        val lineageId = lineageService.create(lineage)

        val uri = URI.create("/api/lineage/${lineageId}")
        return created(uri).build()
    }

    @Operation(
        description = "Get lineage by id",
        responses = [
            ApiResponse(
                description = "Lineage data", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = LineageDTO::class))
                ]
            )
        ]
    )
    @GetMapping("/{lineageId}", produces = [APPLICATION_JSON_VALUE])
    fun lineageById(@PathVariable("lineageId") lineageId: Long): LineageDTO = lineageService.findById(lineageId)

    @Operation(
        description = "Update lineage",
        responses = [ApiResponse(description = "Lineage is updated", responseCode = "200", content = [Content()])]
    )
    @PutMapping("/{lineageId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable("lineageId") lineageId: Long, @RequestBody lineage: LineageDTO): ResponseEntity<Unit> {
        lineageService.update(lineageId, lineage)

        return ok().build()
    }

    @Operation(
        description = "Delete lineage",
        responses = [ApiResponse(description = "Lineage is deleted", responseCode = "204", content = [Content()])]
    )
    @DeleteMapping("/{lineageId}")
    fun delete(@PathVariable("lineageId") lineageId: Long): ResponseEntity<Unit> {
        lineageService.delete(lineageId)

        return noContent().build()
    }

    @Operation(
        description = "Imports new lineage from client",
        responses = [ApiResponse(
            description = "Lineage is created",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = LOCATION, description = "url to new lineage")]
        )]
    )
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

    @Operation(
        description = "Export lineage to client",
        responses = [ApiResponse(description = "Lineage data", responseCode = "200")]
    )
    @GetMapping("/{lineageId}/export", produces = [APPLICATION_JSON_VALUE, TEXT_CSV, TEXT_GED])
    fun exportLineage(
        @PathVariable("lineageId") lineageId: Long,
        @RequestHeader(ACCEPT, required = false) dataType: String?,
    ): ResponseEntity<ByteArray> {
        val lineage = lineageService.exportLineage(lineageId, dataType)

        return ok(lineage)
    }
}
