package com.koldyr.genealogy.controllers

import java.net.URI
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.services.FamilyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
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
@RequestMapping("/api/genealogy/family")
class FamilyController(private val familyService: FamilyService) {

    @GetMapping("/")
    fun families(): Collection<FamilyDTO> = familyService.findAll()

    @PostMapping("/")
    fun create(@RequestBody family: FamilyDTO): ResponseEntity<Unit> {
        val familyId = familyService.create(family)
        return ResponseEntity.created(URI.create("/api/genealogy/family/${familyId}")).build()
    }

    @GetMapping("/{familyId}")
    fun familyById(@PathVariable familyId: Int): FamilyDTO = familyService.findById(familyId)

    @PutMapping("/{familyId}")
    fun update(@PathVariable familyId: Int, @RequestBody family: Family) {
        familyService.update(familyId, family)
    }

    @DeleteMapping("/{familyId}")
    fun delete(@PathVariable familyId: Int): ResponseEntity<Unit> {
        familyService.delete(familyId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{familyId}/event")
    fun createEvent(@PathVariable familyId: Int, @RequestBody event: FamilyEvent): ResponseEntity<Unit> {
        val eventId = familyService.createEvent(familyId, event)

        return ResponseEntity.created(URI.create("/api/genealogy/family/$familyId/event/$eventId")).build()
    }
    
    @GetMapping("/{familyId}/event")
    fun events(@PathVariable familyId: Int): Collection<FamilyEvent> = familyService.findEvents(familyId)

    @DeleteMapping("/{familyId}/event/{eventId}")
    fun deleteEvent(@PathVariable familyId: Int, @PathVariable eventId: Int): ResponseEntity<Unit> {
        familyService.deleteEvent(familyId, eventId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{familyId}/children")
    fun addChild(@PathVariable familyId: Int, @RequestBody child: Person): ResponseEntity<Unit> {
        val childId = familyService.createChild(familyId, child)

        return ResponseEntity.created(URI.create("/api/genealogy/person/$childId")).build()
    }

    @GetMapping("/{familyId}/children")
    fun children(@PathVariable familyId: Int): Collection<Person> = familyService.findChildren(familyId)

    @DeleteMapping("/{familyId}/children/{childId}")
    fun deleteChild(@PathVariable familyId: Int, @PathVariable childId: Int): ResponseEntity<Unit> {
        familyService.deleteChild(familyId, childId)
        return ResponseEntity.noContent().build()
    }

}
