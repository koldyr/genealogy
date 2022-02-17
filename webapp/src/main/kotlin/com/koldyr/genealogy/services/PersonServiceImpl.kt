package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonEventRepository
import com.koldyr.genealogy.persistence.PersonRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Objects.nonNull

/**
 * Description of class PersonServiceImpl
 * @created: 2021-09-28
 */
@Transactional
open class PersonServiceImpl(
        private val personRepository: PersonRepository,
        private val personEventRepository: PersonEventRepository,
        private val familyRepository: FamilyRepository,
        private val mapper: MapperFacade,
        private val userService: UserService) : PersonService {

    override fun findAll(): List<Person> = personRepository.findAllByUser(userService.currentUser())

    override fun create(person: Person): Int {
        person.user = userService.currentUser()
        val saved = personRepository.save(person)
        return saved.id!!
    }

    override fun findById(personId: Int): Person {
        return findPerson(personId)
    }

    override fun update(personId: Int, person: Person) {
        val persisted = findPerson(personId)

        person.id = persisted.id
        person.user = persisted.user
        mapper.map(person, persisted)

        personRepository.save(persisted);
    }

    override fun delete(personId: Int) {
        val person = findPerson(personId)

        val family = if (nonNull(person.familyId)) {
            familyRepository.findById(person.familyId!!)
        } else {
            familyRepository.findChildFamily(person.id!!)
        }
        family.ifPresent {
            it.removePerson(person)
            familyRepository.save(it)
        }

        personRepository.deleteById(personId)
    }

    override fun createEvent(personId: Int, event: PersonEvent): Int {
        event.id = null

        val person = findPerson(personId)
        person.addEvent(event)

        personEventRepository.save(event)
        personRepository.save(person)

        return event.id!!
    }

    override fun findEvents(personId: Int): Collection<PersonEvent> {
        findPerson(personId)
        return personRepository.findEvents(personId)
    }

    override fun deleteEvent(personId: Int, eventId: Int) {
        val person = findPerson(personId)
        findPersonEvent(eventId)
        person.removeEvent(eventId)

        personEventRepository.deleteById(eventId)
        personRepository.save(person)
    }

    private fun findPerson(personId: Int): Person {
        return personRepository.findById(personId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }
    }

    private fun findPersonEvent(personEventId: Int): PersonEvent {
        return personEventRepository.findById(personEventId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Event with id '$personEventId' is not found") }
    }
}
