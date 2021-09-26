package com.koldyr.genealogy.controllers

import java.net.URI
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException

/**
 * Description of class GenealogyController
 * @created: 2021-09-25
 */
@RestController
@RequestMapping("/api/genealogy")
class GenealogyController(
    private val personRepository: PersonRepository,
    private val familyRepository: FamilyRepository) {

    @GetMapping("/")
    fun index(): String {
        return "Greetings from Genealogy"
    }

    @GetMapping("/person")
    fun persons(): List<Person> = personRepository.findAll()

    @GetMapping("/person/{personId}")
    fun personById(@PathVariable personId: Int): Person = personRepository.findById(personId).orElseThrow(::IllegalStateException)

    @PostMapping("/person")
    fun createPerson(person: Person): ResponseEntity<String> {
        val saved = personRepository.save(person)
        return ResponseEntity.created(URI.create("/person/${saved.id}")).build()
    }

    @PutMapping("/person/{personId}")
    fun updatePerson(@PathVariable personId: Int, person: Person) {
        val persisted = personRepository.findById(personId).orElseThrow {
            HttpClientErrorException.create(
                "Person with id '$personId' not found",
                HttpStatus.NOT_FOUND,
                "",
                HttpHeaders.EMPTY,
                ByteArray(0),
                null
            )
        }

        personRepository.save(person);
    }

    @DeleteMapping("/person/{personId}")
    fun deletePersonById(@PathVariable personId: Int) {
        personRepository.deleteById(personId)
    }

}