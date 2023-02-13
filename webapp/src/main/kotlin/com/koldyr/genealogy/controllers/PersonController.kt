package com.koldyr.genealogy.controllers

import java.net.URI
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
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
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import com.koldyr.genealogy.dto.ErrorResponse
import com.koldyr.genealogy.dto.PageResultDTO
import com.koldyr.genealogy.dto.SearchDTO
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.services.PersonService

const val IMAGE_JPG_VALUE = "image/jpg"

/**
 * Description of class PersonController
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/lineage")
@Tags(value = [Tag(name = "PersonController")])
@ApiResponse(
    description = "Internal server error",
    responseCode = "500",
    content = [Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = ErrorResponse::class))]
)
class PersonController(private val personService: PersonService) {

    @Operation(
        description = "Search person in lineage by criteria",
        responses = [
            ApiResponse(
                description = "List of persons", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = PageResultDTO::class))
                ]
            )
        ]
    )
    @PostMapping("/{lineageId}/persons/search", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun search(@PathVariable("lineageId") lineageId: Long, @RequestBody criteria: SearchDTO): PageResultDTO<Person> = personService.search(lineageId, criteria)

    @Operation(
        description = "Fetches list of all persons in lineage",
        responses = [
            ApiResponse(
                description = "List of persons", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = Person::class)))
                ]
            )
        ]
    )
    @GetMapping("/{lineageId}/persons", produces = [APPLICATION_JSON_VALUE])
    fun persons(@PathVariable("lineageId") lineageId: Long): Collection<Person> = personService.findAll(lineageId)

    @Operation(
        description = "Creates new person in lineage",
        responses = [ApiResponse(
            description = "Person is created",
            responseCode = "201",
            content = [Content()],
            headers = [Header(name = LOCATION, description = "url to new person")]
        )]
    )
    @PostMapping("/{lineageId}/persons", consumes = [APPLICATION_JSON_VALUE])
    fun create(@PathVariable("lineageId") lineageId: Long, @RequestBody person: Person): ResponseEntity<Unit> {
        person.lineageId = lineageId
        val personId = personService.create(person)

        val uri = URI.create("/api/lineage/$lineageId/persons/$personId")
        return created(uri).build()
    }

    @Operation(
        description = "Updates person's attributes",
        responses = [ApiResponse(description = "Person is updated", responseCode = "200", content = [Content()])]
    )
    @PutMapping("/{lineageId}/persons/{personId}")
    fun update(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long, @RequestBody person: Person): ResponseEntity<Unit> {
        personService.update(personId, person)

        return ok().build()
    }

    @Operation(
        description = "Get person by id",
        responses = [
            ApiResponse(
                description = "Person data", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, schema = Schema(implementation = Person::class))
                ]
            )
        ]
    )
    @GetMapping("/{lineageId}/persons/{personId}", produces = [APPLICATION_JSON_VALUE])
    fun personById(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long): Person = personService.findById(personId)

    @Operation(
        description = "Deletes person from lineage",
        responses = [ApiResponse(description = "Person is deleted", responseCode = "204", content = [Content()])]
    )
    @DeleteMapping("/{lineageId}/persons/{personId}")
    fun delete(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long): ResponseEntity<Unit> {
        personService.delete(personId)

        return noContent().build()
    }

    @Operation(
        description = "Creates person event",
        responses = [ApiResponse(
            description = "Person is updated",
            responseCode = "200",
            content = [Content()],
            headers = [Header(name = LOCATION, description = "url to new event")]
        )]
    )
    @PostMapping("/{lineageId}/persons/{personId}/events", consumes = [APPLICATION_JSON_VALUE])
    fun createEvent(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long, @RequestBody event: PersonEvent): ResponseEntity<Unit> {
        val eventId = personService.createEvent(personId, event)

        val uri = URI.create("/api/lineage/$lineageId/persons/$personId/events/$eventId")
        return created(uri).build()
    }

    @Operation(
        description = "Fetches list of all events for person",
        responses = [
            ApiResponse(
                description = "List of persons", responseCode = "200", content = [
                    Content(mediaType = APPLICATION_JSON_VALUE, array = ArraySchema(schema = Schema(implementation = PersonEvent::class)))
                ]
            )
        ]
    )
    @GetMapping("/{lineageId}/persons/{personId}/events", produces = [APPLICATION_JSON_VALUE])
    fun events(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long): Collection<PersonEvent> = personService.findEvents(personId)

    @Operation(
        description = "Deletes event from person",
        responses = [ApiResponse(description = "Event is deleted", responseCode = "204", content = [Content()])]
    )
    @DeleteMapping("/{lineageId}/persons/{personId}/events/{eventId}")
    fun deleteEvent(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long, @PathVariable("eventId") eventId: Long): ResponseEntity<Unit> {
        personService.deleteEvent(personId, eventId)

        return noContent().build()
    }

    @PostMapping("/{lineageId}/persons/{personId}/photo", consumes = [IMAGE_JPEG_VALUE, IMAGE_JPG_VALUE, IMAGE_PNG_VALUE])
    fun createPhoto(
        @PathVariable("lineageId") lineageId: Long,
        @PathVariable("personId") personId: Long,
        @RequestHeader(CONTENT_TYPE) imageType: String,
        @RequestBody photo: ByteArray
    ): ResponseEntity<Unit> {
        if (!(imageType == IMAGE_JPEG_VALUE || imageType == IMAGE_JPG_VALUE || imageType == IMAGE_PNG_VALUE)) {
            throw ResponseStatusException(BAD_REQUEST, "Supported image types: jpeg/png")
        }
        if (photo.size > 300 * 1024) {
            throw ResponseStatusException(BAD_REQUEST, "Supported image size < 300 kB")
        }

        val photoUrl = personService.createPhoto(lineageId, personId, imageType, photo)

        val uri = URI.create(photoUrl)
        return created(uri).build()
    }

    @GetMapping("/{lineageId}/persons/{personId}/photo", produces = [IMAGE_JPEG_VALUE, IMAGE_JPG_VALUE, IMAGE_PNG_VALUE])
    @ResponseBody
    fun photo(@PathVariable("lineageId") lineageId: Long, @PathVariable("personId") personId: Long): ResponseEntity<Resource> {
        val personPhoto = personService.photo(personId)
        return ok()
            .header(CONTENT_TYPE, IMAGE_JPEG_VALUE)
            .header(CONTENT_DISPOSITION, "inline; filename=\"avatar${personId}.jpg\"")
            .body(InputStreamResource(personPhoto))
    }
}
