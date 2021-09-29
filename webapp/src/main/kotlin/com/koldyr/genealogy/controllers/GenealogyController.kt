package com.koldyr.genealogy.controllers

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.services.GenealogyService
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/genealogy")
class GenealogyController(private val genealogyService: GenealogyService) {

    @GetMapping("/person")
    fun persons(): Collection<Person> {
        return genealogyService.findAllPersons()
    }

    @GetMapping("/family")
    fun families(): Collection<FamilyDTO> = genealogyService.findAllFamilies()

    @GetMapping("/person/{personId}")
    fun personById(@PathVariable personId: Int): Person = genealogyService.findPersonById(personId)

    @PostMapping("/person")
    fun createPerson(@RequestBody person: Person): ResponseEntity<String> {
        val personId: Int = genealogyService.create(person)
        return ResponseEntity.created(URI.create("/person/$personId")).build()
    }

    @PutMapping("/person/{personId}")
    fun updatePerson(@PathVariable personId: Int, person: Person) {
        genealogyService.update(personId, person)
    }

    @DeleteMapping("/person/{personId}")
    fun deletePerson(@PathVariable personId: Int) {
        genealogyService.deletePerson(personId)
    }

    @PostMapping("/person/{personId}/event")
    fun createPersonEvent(@PathVariable personId: Int, @RequestBody event: PersonEvent): ResponseEntity<Unit> {
        genealogyService.createPersonEvent(personId, event)

        return ResponseEntity.created(URI.create("/person/$personId/event/${event.id}")).build()
    }

    @GetMapping("/person/{personId}/event/{eventId}")
    fun personEvent(@PathVariable personId: Int, @PathVariable eventId: Int): PersonEvent {
        val person = genealogyService.findPersonById(personId)
        return person.events.first { event -> event.id === eventId }
    }

    @GetMapping("/person/{personId}/event")
    fun personEvents(@PathVariable personId: Int): Collection<PersonEvent> {
        val person = genealogyService.findPersonById(personId)
        return person.events
    }

    @DeleteMapping("/person/{personId}/event/{eventId}")
    fun deletePersonEvent(@PathVariable personId: Int, @PathVariable eventId: Int) {
        genealogyService.deletePersonEvent(personId, eventId)
    }
}
