package com.koldyr.genealogy.services

import java.io.InputStream
import java.io.InputStream.*
import java.util.Objects.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import ma.glasnost.orika.MapperFacade
import com.koldyr.genealogy.dto.PageResultDTO
import com.koldyr.genealogy.dto.SearchDTO
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonEventRepository
import com.koldyr.genealogy.persistence.PersonRepository

/**
 * Description of class PersonServiceImpl
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
@Transactional
class PersonServiceImpl(
    private val personRepository: PersonRepository,
    private val personEventRepository: PersonEventRepository,
    private val familyRepository: FamilyRepository,
    private val mapper: MapperFacade,
    private val userService: UserService
) : PersonService {

    private val predicateBuilder = PredicateBuilder()

    @Transactional(readOnly = true)
    override fun findAll(lineageId: Long): List<Person> = personRepository.findAllByUserAndLineageId(userService.currentUser(), lineageId)

    @Transactional(readOnly = true)
    override fun search(lineageId: Long, criteria: SearchDTO): PageResultDTO<Person> {
        val filter = predicateBuilder.personFilter(lineageId, criteria, userService.currentUser().id!!)

        val page = criteria.page?.index ?: 0
        val size = criteria.page?.size ?: 100
        val direction = if (criteria.sort == null) Sort.Direction.ASC else Sort.Direction.fromString(criteria.sort!!.order)
        val property = criteria.sort?.name ?: "id"
        val pageSelector = PageRequest.of(page, size, direction, property)

        val result = personRepository.findAll(filter, pageSelector)
        return createPageResult(result)
    }

    @PreAuthorize("hasRole('user')")
    override fun create(person: Person): Long {
        person.id = null
        person.user = userService.currentUser()
        person.events.forEach { it.person = person }

        val saved = personRepository.save(person)
        return saved.id!!
    }

    @Transactional(readOnly = true)
    override fun findById(personId: Long): Person = findPerson(personId)

    override fun update(personId: Long, person: Person) {
        val persisted = findPerson(personId)

        person.id = persisted.id
        person.user = persisted.user
        person.photo = persisted.photo
        person.lineageId = persisted.lineageId
        mapper.map(person, persisted)

        personRepository.save(persisted)
    }

    override fun delete(personId: Long) {
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

    override fun createEvent(personId: Long, event: PersonEvent): Long {
        event.id = null

        val person = findPerson(personId)
        person.addEvent(event)

        personEventRepository.save(event)
        personRepository.save(person)

        return event.id!!
    }

    @Transactional(readOnly = true)
    override fun findEvents(personId: Long): Collection<PersonEvent> {
        findPerson(personId)
        return personRepository.findEvents(personId)
    }

    override fun deleteEvent(personId: Long, eventId: Long) {
        val person = findPerson(personId)
        findPersonEvent(eventId)
        person.removeEvent(eventId)

        personEventRepository.deleteById(eventId)
        personRepository.save(person)
    }

    @Transactional(readOnly = true)
    override fun photo(personId: Long): InputStream {
        val person = findPerson(personId)
        return person.photo?.inputStream() ?: nullInputStream()
    }

    override fun createPhoto(lineageId: Long, personId: Long, type: String, photo: ByteArray): String {
        val person = findPerson(personId)
        person.photo = photo
        person.photoUrl = "lineage/$lineageId/persons/$personId/photo"
        personRepository.save(person)

        return person.photoUrl!!
    }

    private fun findPerson(personId: Long): Person {
        val person = personRepository.findById(personId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$personId' is not found") }

        val currentUser = userService.currentUser()
        if (currentUser.hasRole("user") && person.user != currentUser) {
            throw ResponseStatusException(UNAUTHORIZED, "You don't have access to Person with id '$personId'")
        }

        return person
    }

    private fun findPersonEvent(personEventId: Long): PersonEvent {
        return personEventRepository.findById(personEventId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Event with id '$personEventId' is not found") }
    }

    private fun <I> createPageResult(contentPage: Page<I>): PageResultDTO<I> {
        val pageResult = PageResultDTO<I>(contentPage.content)
        pageResult.total = contentPage.totalElements
        pageResult.page = contentPage.number
        pageResult.size = contentPage.size
        return pageResult
    }
}
