package com.koldyr.genealogy.services

import java.util.Objects.nonNull
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonEventRepository
import com.koldyr.genealogy.persistence.PersonRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class PersonServiceImpl
 * @created: 2021-09-28
 */
open class PersonServiceImpl(
        private val personRepository: PersonRepository,
        private val personEventRepository: PersonEventRepository,
        private val familyRepository: FamilyRepository,
        private val mapper: MapperFacade) : PersonService {

    override fun findAll(): List<Person> = personRepository.findAll()

    @Transactional
    override fun create(person: Person): Int {
        val saved = personRepository.save(person)
        return saved.id!!
    }

    override fun findById(personId: Int): Person {
        return find(personId)
    }

    @Transactional
    override fun update(personId: Int, person: Person) {
        val persisted = find(personId)

        person.id = persisted.id
        mapper.map(person, persisted)

        personRepository.save(persisted);
    }

    @Transactional
    override fun delete(personId: Int) {
        val person = find(personId)

        val family = if (nonNull(person.familyId)) {
            familyRepository.findById(person.familyId!!)
        } else {
            familyRepository.findChild(person.id!!)
        }
        family.ifPresent {
            it.removePerson(person)
            familyRepository.save(it)
        }

        personRepository.deleteById(personId)
    }

    @Transactional
    override fun createEvent(personId: Int, event: PersonEvent): Int {
        event.id = null

        val person = find(personId)
        person.addEvent(event)

        personEventRepository.save(event)
        personRepository.save(person)
        
        return event.id!!
    }

    override fun findEvents(personId: Int): Collection<PersonEvent> = personRepository.findEvents(personId)

    @Transactional
    override fun deleteEvent(personId: Int, eventId: Int) {
        val person = find(personId)
        person.removeEvent(eventId)

        personEventRepository.deleteById(eventId)
        personRepository.save(person)
    }

    private fun find(personId: Int): Person {
        return personRepository.findById(personId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }
    }
}
