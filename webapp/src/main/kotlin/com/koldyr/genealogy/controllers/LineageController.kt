package com.koldyr.genealogy.controllers

import java.net.URI
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.koldyr.genealogy.Secured
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.services.LineageService

/**
 * Description of the LineageController class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
@RestController
@RequestMapping("/api/lineage")
@Secured
class LineageController(private val lineageService: LineageService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun lineages(): Collection<LineageDTO> = lineageService.findAll()

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody lineage: LineageDTO): ResponseEntity<Unit> {
        val lineageId = lineageService.create(lineage)

        val uri = URI.create("/api/lineage/${lineageId}")
        return ResponseEntity.created(uri).build()
    }

    @GetMapping("/{lineageId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun lineageById(@PathVariable lineageId: Long): LineageDTO = lineageService.findById(lineageId)

    @PutMapping("/{lineageId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable lineageId: Long, @RequestBody lineage: LineageDTO): ResponseEntity<Unit> {
        lineageService.update(lineageId, lineage)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{lineageId}")
    fun delete(@PathVariable lineageId: Long): ResponseEntity<Unit> {
        lineageService.delete(lineageId)

        return ResponseEntity.noContent().build()
    }
}