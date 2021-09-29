package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import java.util.stream.Collectors.*

/**
 * Description of class GenealogyServiceImpl
 * @created: 2021-09-28
 */
open class GenealogyServiceImpl(
        private val personRepository: PersonRepository,
        private val familyRepository: FamilyRepository): GenealogyService {
    
    @Transactional
    override fun create(person: Person): Int {
        val saved = personRepository.save(person)
        return saved.id!!
    }

    override fun findAllPersons(): List<Person> {
        val order = Sort.Order.asc("id")
        return personRepository.findAll(Sort.by(order))
    }

    override fun findPersonById(personId: Int): Person {
        return personRepository.findById(personId)
                .orElseThrow { HttpClientErrorException(NOT_FOUND, "Person with id '$personId' is not found") }
    }

    @Transactional
    override fun update(personId: Int, person: Person) {
        val persisted: Person = personRepository.findById(personId)
                .orElseThrow { HttpClientErrorException(NOT_FOUND, "Person with id '$personId' is not found") }

        persisted.name = person.name
        persisted.note = person.note
        persisted.occupation = person.occupation
        persisted.place = person.place

        personRepository.save(person);
    }

    @Transactional
    override fun deletePerson(personId: Int) {
        personRepository.deleteById(personId)
    }

    @Transactional
    override fun createPersonEvent(personId: Int, event: PersonEvent) {
        val person: Person = personRepository.findById(personId)
                .orElseThrow { HttpClientErrorException(NOT_FOUND, "Person with id '$personId' is not found") }
        personRepository.save(person)
    }

    @Transactional
    override fun deletePersonEvent(personId: Int, eventId: Int) {
        val person = personRepository.findById(personId)
                .orElseThrow { HttpClientErrorException(NOT_FOUND, "Person with id '$personId' is not found") }
        person.events.removeIf { it.id == eventId }
        personRepository.save(person)
    }

    override fun findAllFamilies(): List<FamilyDTO> {
        return familyRepository.findAll().stream()
                .map {
                    val family = FamilyDTO(it.id!!)
                    family.husband = it.husband?.id
                    family.wife = it.wife?.id

                    val children: MutableList<Int> = it.children.stream().map(Person::id).collect(toList())
                    family.children = children.toList()

                    family
                }
                .collect(toList())
    }
}
