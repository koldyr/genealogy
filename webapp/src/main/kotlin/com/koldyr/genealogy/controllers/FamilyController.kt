package com.koldyr.genealogy.controllers

import java.net.URI
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.security.Secured
import com.koldyr.genealogy.services.FamilyService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.noContent
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Description of class FamilyController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/genealogy/families")
@Secured
class FamilyController(private val familyService: FamilyService) {

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun families(): Collection<FamilyDTO> = familyService.findAll()

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun create(@RequestBody family: FamilyDTO): ResponseEntity<Unit> {
        val familyId = familyService.create(family)

        val uri = URI.create("/api/genealogy/families/${familyId}")
        return created(uri).build()
    }

    @GetMapping("/{familyId}", produces = [APPLICATION_JSON_VALUE])
    fun familyById(@PathVariable familyId: Int): FamilyDTO = familyService.findById(familyId)

    @PutMapping("/{familyId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable familyId: Int, @RequestBody family: FamilyDTO) = familyService.update(familyId, family)

    @DeleteMapping("/{familyId}")
    fun delete(@PathVariable familyId: Int): ResponseEntity<Unit> {
        familyService.delete(familyId)

        return noContent().build()
    }

    @PostMapping("/{familyId}/events", consumes = [APPLICATION_JSON_VALUE])
    fun createEvent(@PathVariable familyId: Int, @RequestBody event: FamilyEvent): ResponseEntity<Unit> {
        val eventId = familyService.createEvent(familyId, event)

        val uri = URI.create("/api/genealogy/families/$familyId/events/$eventId")
        return created(uri).build()
    }

    @GetMapping("/{familyId}/events", produces = [APPLICATION_JSON_VALUE])
    fun events(@PathVariable familyId: Int): Collection<FamilyEvent> = familyService.findEvents(familyId)

    @DeleteMapping("/{familyId}/events/{eventId}")
    fun deleteEvent(@PathVariable familyId: Int, @PathVariable eventId: Int): ResponseEntity<Unit> {
        familyService.deleteEvent(familyId, eventId)

        return noContent().build()
    }

    @PostMapping("/{familyId}/children", consumes = [APPLICATION_JSON_VALUE])
    fun createChild(@PathVariable familyId: Int, @RequestBody child: Person): ResponseEntity<Unit> {
        val childId = familyService.createChild(familyId, child)

        val uri = URI.create("/api/genealogy/persons/$childId")
        return created(uri).build()
    }

    @PatchMapping("/{familyId}/children/{childId}")
    fun addChild(@PathVariable familyId: Int, @PathVariable childId: Int): ResponseEntity<Unit> {
        familyService.addChild(familyId, childId)

        val uri = URI.create("/api/genealogy/persons/$childId")
        return created(uri).build()
    }

    @GetMapping("/{familyId}/children", produces = [APPLICATION_JSON_VALUE])
    fun children(@PathVariable familyId: Int): Collection<Person> = familyService.findChildren(familyId)

    @DeleteMapping("/{familyId}/children/{childId}")
    fun deleteChild(@PathVariable familyId: Int, @PathVariable childId: Int): ResponseEntity<Unit> {
        familyService.deleteChild(familyId, childId)

        return noContent().build()
    }
}
