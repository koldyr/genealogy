package com.koldyr.genealogy.controllers

import java.net.URI
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.services.FamilyService

/**
 * Description of class FamilyController
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/lineage/v1")
@Tags(value = [Tag(name = "FamilyController")])
class FamilyController(
    private val familyService: FamilyService
) : BaseController() {

    @Operation(
        description = "List of families in lineage",
        responses = [
            ApiResponse(description = "List of families", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = FamilyDTO::class)))])
        ]
    )
    @GetMapping("/{lineageId}/families", produces = [APPLICATION_JSON_VALUE])
    fun families(@PathVariable("lineageId") lineageId: Long): Collection<FamilyDTO> = familyService.findAll(lineageId)

    @Operation(
        description = "Creates new family in lineage",
        responses = [ApiResponse(
            description = "Family is created",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = HttpHeaders.LOCATION, description = "url to new family")]
        )]
    )
    @PostMapping("/{lineageId}/families", consumes = [APPLICATION_JSON_VALUE])
    fun create(@PathVariable("lineageId") lineageId: Long, @RequestBody family: FamilyDTO): ResponseEntity<Unit> {
        val familyId = familyService.create(lineageId, family)

        val uri = URI.create("/api/lineage/v1/$lineageId/families/${familyId}")
        return created(uri).build()
    }

    @Operation(
        description = "Get family by id",
        responses = [
            ApiResponse(description = "List of persons", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = FamilyDTO::class))])
        ]
    )
    @GetMapping("/{lineageId}/families/{familyId}", produces = [APPLICATION_JSON_VALUE])
    fun familyById(@PathVariable("lineageId") lineageId: Long, @PathVariable("familyId") familyId: Long): FamilyDTO =
        familyService.findById(familyId)
                                                                                                              
    @Operation(
        description = "Update family",
        responses = [ApiResponse(description = "Family is updated", responseCode = "200", content = [Content()])]
    )
    @PutMapping("/{lineageId}/families/{familyId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable("lineageId") lineageId: Long,
               @PathVariable("familyId") familyId: Long,
               @RequestBody family: FamilyDTO): ResponseEntity<Unit> {
        familyService.update(familyId, family)

        return ok().build()
    }

    @Operation(
        description = "Delete family from lineage",
        responses = [ApiResponse(description = "Family is deleted", responseCode = "204", content = [Content()])]
    )
    @DeleteMapping("/{lineageId}/families/{familyId}")
    fun delete(@PathVariable("lineageId") lineageId: Long, @PathVariable("familyId") familyId: Long): ResponseEntity<Unit> {
        familyService.delete(familyId)

        return noContent().build()
    }

    @Operation(
        description = "Creates new family event for family",
        responses = [ApiResponse(
            description = "Family event is created",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = HttpHeaders.LOCATION, description = "url to new event")]
        )]
    )
    @PostMapping("/{lineageId}/families/{familyId}/events", consumes = [APPLICATION_JSON_VALUE])
    fun createEvent(@PathVariable("lineageId") lineageId: Long,
                    @PathVariable("familyId") familyId: Long,
                    @RequestBody @Valid event: FamilyEvent): ResponseEntity<Unit> {
        val eventId = familyService.createEvent(familyId, event)

        val uri = URI.create("/api/lineage/v1/$lineageId/families/$familyId/events/$eventId")
        return created(uri).build()
    }

    @Operation(
        description = "List of events for family",
        responses = [
            ApiResponse(description = "List of events", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = FamilyEvent::class)))])
        ]
    )
    @GetMapping("/{lineageId}/families/{familyId}/events", produces = [APPLICATION_JSON_VALUE])
    fun events(@PathVariable("lineageId") lineageId: Long, @PathVariable("familyId") familyId: Long): Collection<FamilyEvent> =
        familyService.findEvents(familyId)

    @Operation(
        description = "Delete event from family",
        responses = [ApiResponse(description = "Event is deleted", responseCode = "204", content = [Content()])]
    )
    @DeleteMapping("/{lineageId}/families/{familyId}/events/{eventId}")
    fun deleteEvent(@PathVariable("lineageId") lineageId: Long,
                    @PathVariable("familyId") familyId: Long,
                    @PathVariable("eventId") eventId: Long): ResponseEntity<Unit> {
        familyService.deleteEvent(familyId, eventId)

        return noContent().build()
    }

    @Operation(
        description = "Creates new child in family",
        responses = [ApiResponse(
            description = "Child is created",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = HttpHeaders.LOCATION, description = "url to new child")]
        )]
    )
    @PostMapping("/{lineageId}/families/{familyId}/children", consumes = [APPLICATION_JSON_VALUE])
    fun createChild(@PathVariable("lineageId") lineageId: Int,
                    @PathVariable("familyId") familyId: Long,
                    @RequestBody @Valid child: Person): ResponseEntity<Unit> {
        val childId = familyService.createChild(familyId, child)

        val uri = URI.create("/api/lineage/v1/$lineageId/persons/$childId")
        return created(uri).build()
    }

    @Operation(
        description = "Appends child to family",
        responses = [ApiResponse(
            description = "Child is added",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = HttpHeaders.LOCATION, description = "url to child")]
        )]
    )
    @PatchMapping("/{lineageId}/families/{familyId}/children/{childId}")
    fun addChild(@PathVariable("lineageId") lineageId: Long,
                 @PathVariable("familyId") familyId: Long,
                 @PathVariable("childId") childId: Long): ResponseEntity<Unit> {
        familyService.addChild(familyId, childId)

        val uri = URI.create("/api/lineage/v1/$lineageId/persons/$childId")
        return created(uri).build()
    }

    @Operation(
        description = "List of children in family",
        responses = [
            ApiResponse(description = "List of children", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = Person::class)))])
        ]
    )
    @GetMapping("/{lineageId}/families/{familyId}/children", produces = [APPLICATION_JSON_VALUE])
    fun children(@PathVariable("lineageId") lineageId: Long, @PathVariable("familyId") familyId: Long): Collection<Person> =
        familyService.findChildren(familyId)

    @Operation(
        description = "Delete child from family",
        responses = [ApiResponse(description = "Child is deleted", responseCode = "204", content = [Content()])]
    )
    @DeleteMapping("/{lineageId}/families/{familyId}/children/{childId}")
    fun deleteChild(@PathVariable("lineageId") lineageId: Long,
                    @PathVariable("familyId") familyId: Long,
                    @PathVariable("childId") childId: Long): ResponseEntity<Unit> {
        familyService.deleteChild(familyId, childId)

        return noContent().build()
    }
}
