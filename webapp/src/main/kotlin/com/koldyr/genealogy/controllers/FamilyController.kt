package com.koldyr.genealogy.controllers

import java.net.URI
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.koldyr.genealogy.Secured
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
@RequestMapping("/api/lineage")
@Secured
class FamilyController(private val familyService: FamilyService) {

    @GetMapping("/{lineageId}/families", produces = [APPLICATION_JSON_VALUE])
    fun families(@PathVariable lineageId: Long): Collection<FamilyDTO> = familyService.findAll(lineageId)

    @PostMapping("/{lineageId}/families", consumes = [APPLICATION_JSON_VALUE])
    fun create(@PathVariable lineageId: Long, @RequestBody family: FamilyDTO): ResponseEntity<Unit> {
        val familyId = familyService.create(family)

        val uri = URI.create("/api/lineage/$lineageId/families/${familyId}")
        return created(uri).build()
    }

    @GetMapping("/{lineageId}/families/{familyId}", produces = [APPLICATION_JSON_VALUE])
    fun familyById(@PathVariable lineageId: Long, @PathVariable familyId: Long): FamilyDTO = familyService.findById(familyId)

    @PutMapping("/{lineageId}/families/{familyId}", consumes = [APPLICATION_JSON_VALUE])
    fun update(@PathVariable lineageId: Long, @PathVariable familyId: Long, @RequestBody family: FamilyDTO): ResponseEntity<Unit> {
        familyService.update(familyId, family)
        
        return ok().build()
    }

    @DeleteMapping("/{lineageId}/families/{familyId}")
    fun delete(@PathVariable lineageId: Long, @PathVariable familyId: Long): ResponseEntity<Unit> {
        familyService.delete(familyId)

        return noContent().build()
    }

    @PostMapping("/{lineageId}/families/{familyId}/events", consumes = [APPLICATION_JSON_VALUE])
    fun createEvent(@PathVariable lineageId: Long, @PathVariable familyId: Long, @RequestBody event: FamilyEvent): ResponseEntity<Unit> {
        val eventId = familyService.createEvent(familyId, event)

        val uri = URI.create("/api/lineage/$lineageId/families/$familyId/events/$eventId")
        return created(uri).build()
    }

    @GetMapping("/{lineageId}/families/{familyId}/events", produces = [APPLICATION_JSON_VALUE])
    fun events(@PathVariable lineageId: Long, @PathVariable familyId: Long): Collection<FamilyEvent> = familyService.findEvents(familyId)

    @DeleteMapping("/{lineageId}/families/{familyId}/events/{eventId}")
    fun deleteEvent(@PathVariable lineageId: Long, @PathVariable familyId: Long, @PathVariable eventId: Long): ResponseEntity<Unit> {
        familyService.deleteEvent(familyId, eventId)

        return noContent().build()
    }

    @PostMapping("/{lineageId}/families/{familyId}/children", consumes = [APPLICATION_JSON_VALUE])
    fun createChild(@PathVariable lineageId: Int, @PathVariable familyId: Long, @RequestBody child: Person): ResponseEntity<Unit> {
        val childId = familyService.createChild(familyId, child)

        val uri = URI.create("/api/lineage/$lineageId/persons/$childId")
        return created(uri).build()
    }

    @PatchMapping("/{lineageId}/families/{familyId}/children/{childId}")
    fun addChild(@PathVariable lineageId: Long, @PathVariable familyId: Long, @PathVariable childId: Long): ResponseEntity<Unit> {
        familyService.addChild(familyId, childId)

        val uri = URI.create("/api/lineage/$lineageId/persons/$childId")
        return created(uri).build()
    }

    @GetMapping("/{lineageId}/families/{familyId}/children", produces = [APPLICATION_JSON_VALUE])
    fun children(@PathVariable lineageId: Long, @PathVariable familyId: Long): Collection<Person> = familyService.findChildren(familyId)

    @DeleteMapping("/{lineageId}/families/{familyId}/children/{childId}")
    fun deleteChild(@PathVariable lineageId: Long, @PathVariable familyId: Long, @PathVariable childId: Long): ResponseEntity<Unit> {
        familyService.deleteChild(familyId, childId)

        return noContent().build()
    }
}
