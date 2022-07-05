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
import com.koldyr.genealogy.Secured
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
@Secured
class PersonController(private val personService: PersonService) {

    @PostMapping("/{lineageId}/persons/search", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun search(@PathVariable lineageId: Long, @RequestBody criteria: SearchDTO): PageResultDTO<Person> = personService.search(lineageId, criteria)

    @GetMapping("/{lineageId}/persons", produces = [APPLICATION_JSON_VALUE])
    fun persons(@PathVariable lineageId: Long): Collection<Person> = personService.findAll(lineageId)

    @PostMapping("/{lineageId}/persons", consumes = [APPLICATION_JSON_VALUE])
    fun create(@PathVariable lineageId: Long, @RequestBody person: Person): ResponseEntity<Unit> {
        person.lineageId = lineageId
        val personId = personService.create(person)

        val uri = URI.create("/api/lineage/$lineageId/persons/$personId")
        return created(uri).build()
    }

    @PutMapping("/{lineageId}/persons/{personId}")
    fun update(@PathVariable lineageId: Long, @PathVariable personId: Long, @RequestBody person: Person): ResponseEntity<Unit> {
        personService.update(personId, person)

        return ok().build()
    }

    @GetMapping("/{lineageId}/persons/{personId}", produces = [APPLICATION_JSON_VALUE])
    fun personById(@PathVariable lineageId: Long, @PathVariable personId: Long): Person = personService.findById(personId)

    @DeleteMapping("/{lineageId}/persons/{personId}")
    fun delete(@PathVariable lineageId: Long, @PathVariable personId: Long): ResponseEntity<Unit> {
        personService.delete(personId)

        return noContent().build()
    }

    @PostMapping("/{lineageId}/persons/{personId}/events", consumes = [APPLICATION_JSON_VALUE])
    fun createEvent(@PathVariable lineageId: Long, @PathVariable personId: Long, @RequestBody event: PersonEvent): ResponseEntity<Unit> {
        val eventId = personService.createEvent(personId, event)

        val uri = URI.create("/api/lineage/$lineageId/persons/$personId/events/$eventId")
        return created(uri).build()
    }

    @GetMapping("/{lineageId}/persons/{personId}/events", produces = [APPLICATION_JSON_VALUE])
    fun events(@PathVariable lineageId: Long, @PathVariable personId: Long): Collection<PersonEvent> = personService.findEvents(personId)

    @DeleteMapping("/{lineageId}/persons/{personId}/events/{eventId}")
    fun deleteEvent(@PathVariable lineageId: Long, @PathVariable personId: Long, @PathVariable eventId: Long): ResponseEntity<Unit> {
        personService.deleteEvent(personId, eventId)

        return noContent().build()
    }

    @PostMapping("/{lineageId}/persons/{personId}/photo", consumes = [IMAGE_JPEG_VALUE, IMAGE_JPG_VALUE, IMAGE_PNG_VALUE])
    fun createPhoto(
        @PathVariable lineageId: Long,
        @PathVariable personId: Long,
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
    fun photo(@PathVariable lineageId: Long, @PathVariable personId: Long): ResponseEntity<Resource> {
        val personPhoto = personService.photo(personId)
        return ok()
            .header(CONTENT_TYPE, IMAGE_JPEG_VALUE)
            .header(CONTENT_DISPOSITION, "inline; filename=\"avatar${personId}.jpg\"")
            .body(InputStreamResource(personPhoto))
    }
}
