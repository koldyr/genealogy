package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.services.PersonService
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Description of class GenealogyController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/genealogy/person/")
class PersonController(private val personService: PersonService) {

    @GetMapping
    fun persons(): Collection<Person> = personService.findAll()

    @PostMapping
    fun create(@RequestBody person: Person): ResponseEntity<String> {
        val personId: Int = personService.create(person)
        
        val uri = URI.create("/api/genealogy/person/$personId")
        return created(uri).build()
    }

    @PutMapping("{personId}")
    fun update(@PathVariable personId: Int, @RequestBody person: Person) = personService.update(personId, person)

    @GetMapping("{personId}")
    fun personById(@PathVariable personId: Int): Person = personService.findById(personId)
    
    @DeleteMapping("{personId}")
    fun delete(@PathVariable personId: Int): ResponseEntity<Unit> {
        personService.delete(personId)
        
        return noContent().build()
    }

    @PostMapping("{personId}/event")
    fun createEvent(@PathVariable personId: Int, @RequestBody event: PersonEvent): ResponseEntity<Unit> {
        val eventId = personService.createEvent(personId, event)

        val uri = URI.create("/api/genealogy/person/$personId/event/$eventId")
        return created(uri).build()
    }

    @GetMapping("{personId}/event/{eventId}")
    fun personEvent(@PathVariable personId: Int, @PathVariable eventId: Int): PersonEvent {
        val person = personService.findById(personId)
        return person.events.first { event -> event.id === eventId }
    }

    @GetMapping("{personId}/event")
    fun events(@PathVariable personId: Int): Collection<PersonEvent> = personService.findEvents(personId)

    @DeleteMapping("person/{personId}/event/{eventId}")
    fun deleteEvent(@PathVariable personId: Int, @PathVariable eventId: Int): ResponseEntity<Unit> {
        personService.deleteEvent(personId, eventId)
        
        return noContent().build()
    }
}
