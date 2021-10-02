package com.koldyr.genealogy.controllers

import java.net.URI
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.services.FamilyService
import org.springframework.http.ResponseEntity
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

    @GetMapping("/{familyId}")
    fun familyById(@PathVariable familyId: Int): FamilyDTO = familyService.findById(familyId)

    @PostMapping("/")
    fun create(@RequestBody family: Family): ResponseEntity<Unit> {
        val familyId = familyService.create(family)
        return ResponseEntity.created(URI.create("/api/genealogy/family/${familyId}")).build()
    }

    @PutMapping("/{familyId}")
    fun update(@RequestBody family: Family): ResponseEntity<Unit> {
        val familyId = familyService.create(family)
        return ResponseEntity.created(URI.create("/api/genealogy/family/${familyId}")).build()
    }

}
