package com.koldyr.genealogy.services

import java.util.stream.Collectors.toList
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class FamilyServiceImpl
 * @created: 2021-09-28
 */
open class FamilyServiceImpl(
    private val familyRepository: FamilyRepository,
    private val personRepository: PersonRepository
) : FamilyService {

    override fun findAll(): List<FamilyDTO> {
        return familyRepository.findAll().stream()
            .map(this::mapFamily)
            .collect(toList())
    }

    @Transactional
    override fun create(family: FamilyDTO): Int {
        val newFamily = Family()

        if (family.husband != null) {
            val husband = personRepository.findById(family.husband!!)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '${family.husband}' is not found") }
            newFamily.husband = husband
        }

        if (family.wife != null) {
            val wife = personRepository.findById(family.wife!!)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '${family.wife}' is not found") }
            newFamily.wife = wife
        }

        if (family.children != null && family.children!!.size > 0) {
            family.children!!.stream()
                .map {
                    personRepository.findById(it)
                        .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '${it}' is not found") }
                }
                .forEach { newFamily.children.add(it) }
        }

        val saved = familyRepository.save(newFamily)
        return saved.id!!
    }

    override fun findById(familyId: Int): FamilyDTO {
        return familyRepository.findById(familyId)
            .map(this::mapFamily)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
    }

    @Transactional
    override fun update(familyId: Int, family: Family) {
        val persisted: Family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }

        persisted.husband = family.husband
        persisted.wife = family.wife

        persisted.children.clear()
        persisted.children.addAll(family.children)

        persisted.events.clear()
        persisted.events.addAll(family.events)

        familyRepository.save(persisted);
    }

    @Transactional
    override fun delete(familyId: Int) {
        familyRepository.deleteById(familyId)
    }

    @Transactional
    override fun createEvent(familyId: Int, event: FamilyEvent): Int {
        val family: Family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
        family.addEvent(event)

        val saved = familyRepository.save(family)
        return saved.id!!
    }

    @Transactional
    override fun deleteEvent(familyId: Int, eventId: Int) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
        family.removeEvent(eventId)
        familyRepository.save(family)
    }

    override fun findEvents(familyId: Int): Collection<FamilyEvent> {
        return familyRepository.findEvents(familyId)
    }

    @Transactional
    override fun createChild(familyId: Int, child: Person): Int {
        val family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }

        val saved = personRepository.save(child)
        family.children.add(saved)

        familyRepository.save(family)
        return saved.id!!
    }

    override fun findChildren(familyId: Int): Collection<Person> {
        val family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
        return family.children
    }

    @Transactional
    override fun deleteChild(familyId: Int, childId: Int) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
        family.children.removeIf { it.id == childId }

        familyRepository.save(family)
    }

    private fun mapFamily(entity: Family): FamilyDTO {
        val dto = FamilyDTO(entity.id!!)
        dto.husband = entity.husband?.id
        dto.wife = entity.wife?.id

        val children: MutableList<Int> = entity.children.stream().map(Person::id).collect(toList())
        dto.children = children.toList()

        val events: MutableList<Int> = entity.events.stream().map(FamilyEvent::id).collect(toList())
        dto.events = events.toList()

        return dto
    }
}
