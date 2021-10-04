package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.persistence.PersonRepository
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class PersonServiceImpl
 * @created: 2021-09-28
 */
open class PersonServiceImpl(
    private val personRepository: PersonRepository
) : PersonService {

    override fun findAll(): List<Person> = personRepository.findAll()
    
    @Transactional
    override fun create(person: Person): Int {
        val saved = personRepository.save(person)
        return saved.id!!
    }

    override fun findById(personId: Int): Person {
        return personRepository.findById(personId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }
    }

    @Transactional
    override fun update(personId: Int, person: Person) {
        val persisted: Person = personRepository.findById(personId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }

        persisted.name = person.name
        persisted.note = person.note
        persisted.occupation = person.occupation
        persisted.place = person.place
        persisted.gender = person.gender

        personRepository.save(persisted);
    }

    @Transactional
    override fun delete(personId: Int) = personRepository.deleteById(personId)

    @Transactional
    override fun createEvent(personId: Int, event: PersonEvent): Int {
        val person: Person = personRepository.findById(personId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }
        person.addEvent(event)

        personRepository.save(person)
        return event.id!!
    }

    override fun findEvents(personId: Int): Collection<PersonEvent> = personRepository.findEvents(personId)

    @Transactional
    override fun deleteEvent(personId: Int, eventId: Int) {
        val person = personRepository.findById(personId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }
        person.removeEvent(eventId)
        personRepository.save(person)
    }
}
