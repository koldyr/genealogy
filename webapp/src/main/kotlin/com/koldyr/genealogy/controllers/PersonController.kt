package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.GenealogyConfig
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.services.PersonService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.noContent
import org.springframework.web.bind.annotation.*
import java.net.URI

/**
 * Description of class PersonController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/genealogy/persons")
@GenealogyConfig.Secured
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
    fun update(@PathVariable personId: Int, @RequestBody person: Person) = personService.update(personId, person)

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
}
