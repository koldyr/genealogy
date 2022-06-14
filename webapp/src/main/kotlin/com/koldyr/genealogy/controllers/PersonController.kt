package com.koldyr.genealogy.controllers

import java.net.URI
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
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
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.security.Secured
import com.koldyr.genealogy.services.PersonService


/**
 * Description of class PersonController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/genealogy/persons")
@Secured
class PersonController(private val personService: PersonService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun persons(): Collection<Person> = personService.findAll()

    @PostMapping
    fun create(@RequestBody person: Person): ResponseEntity<Unit> {
        val personId: Int = personService.create(person)

        val uri = URI.create("/api/genealogy/persons/$personId")
        return created(uri).build()
    }

    @PutMapping("/{personId}")
    fun update(@PathVariable personId: Int, @RequestBody person: Person): ResponseEntity<Unit> {
        personService.update(personId, person)

        return ok().build()
    }

    @GetMapping("/{personId}", produces = [APPLICATION_JSON_VALUE])
    fun personById(@PathVariable personId: Int): Person = personService.findById(personId)

    @DeleteMapping("/{personId}")
    fun delete(@PathVariable personId: Int): ResponseEntity<Unit> {
        personService.delete(personId)

        return noContent().build()
    }

    @PostMapping("/{personId}/events", consumes = [APPLICATION_JSON_VALUE])
    fun createEvent(@PathVariable personId: Int, @RequestBody event: PersonEvent): ResponseEntity<Unit> {
        val eventId = personService.createEvent(personId, event)

        val uri = URI.create("/api/genealogy/persons/$personId/events/$eventId")
        return created(uri).build()
    }

    @GetMapping("/{personId}/events", produces = [APPLICATION_JSON_VALUE])
    fun events(@PathVariable personId: Int): Collection<PersonEvent> = personService.findEvents(personId)

    @DeleteMapping("/{personId}/events/{eventId}")
    fun deleteEvent(@PathVariable personId: Int, @PathVariable eventId: Int): ResponseEntity<Unit> {
        personService.deleteEvent(personId, eventId)

        return noContent().build()
    }

    @PostMapping("/{personId}/photo", consumes = [IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE])
    fun createPhoto(@PathVariable personId: Int,
                    @RequestHeader("Content-Type") imageType: String,
                    @RequestBody photo: ByteArray): ResponseEntity<Unit> {
        if (!(imageType == IMAGE_JPEG_VALUE || imageType == IMAGE_PNG_VALUE)) {
            throw ResponseStatusException(BAD_REQUEST, "Supported image types: jpeg/png")
        }
        if (photo.size > 100 * 1024) {
            throw ResponseStatusException(BAD_REQUEST, "Supported image size < 100 kB")
        }

        val photoUrl = personService.createPhoto(personId, imageType, photo)

        val uri = URI.create(photoUrl)
        return created(uri).build()
    }

    @GetMapping("/{personId}/photo", produces = [IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE])
    @ResponseBody
    fun photo(@PathVariable personId: Int): ResponseEntity<Resource> {
        val personPhoto = personService.photo(personId)
        return ResponseEntity
            .status(OK)
            .header("Content-Type", IMAGE_JPEG_VALUE)
            .header("Content-Disposition", "inline; filename=\"avatar${personId}.jpg\"")
            .body(InputStreamResource(personPhoto))
    }
}
