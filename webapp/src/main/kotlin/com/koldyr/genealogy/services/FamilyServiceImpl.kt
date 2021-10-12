package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyEventRepository
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Objects.*

/**
 * Description of class FamilyServiceImpl
 * @created: 2021-09-28
 */
open class FamilyServiceImpl(
    private val familyRepository: FamilyRepository,
    private val personRepository: PersonRepository,
    private val familyEventRepository: FamilyEventRepository,
    private val mapper: MapperFacade
) : FamilyService {

    override fun findAll(): List<FamilyDTO> {
        return familyRepository.findAll().map(this::mapFamily)
    }

    @Transactional
    override fun create(family: FamilyDTO): Int {
        val newFamily = mapper.map(family, Family::class.java)

        if (nonNull(newFamily.husband) && nonNull(newFamily.husband?.familyId)) {
            throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.husband}' already in family ${newFamily.husband?.familyId}")
        }
        if (nonNull(newFamily.wife) && nonNull(newFamily.wife?.familyId)) {
            throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.wife}' already in family ${newFamily.wife?.familyId}")
        }

        val saved = familyRepository.save(newFamily)

        if (nonNull(newFamily.husband)) {
            newFamily.husband?.familyId = saved.id
            personRepository.save(newFamily.husband!!)
        }
        if (nonNull(newFamily.wife)) {
            newFamily.wife?.familyId = saved.id
            personRepository.save(newFamily.wife!!)
        }

        return saved.id!!
    }

    override fun findById(familyId: Int): FamilyDTO {
        return familyRepository.findById(familyId)
            .map(this::mapFamily)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
    }

    @Transactional
    override fun update(familyId: Int, family: FamilyDTO) {
        val persisted = find(familyId)

        family.id = persisted.id
        mapper.map(family, persisted)

        familyRepository.save(persisted);
    }

    @Transactional
    override fun delete(familyId: Int) = familyRepository.deleteById(familyId)

    @Transactional
    override fun createEvent(familyId: Int, event: FamilyEvent): Int {
        val family = find(familyId)
        
        event.id = null
        family.addEvent(event)

        familyEventRepository.save(event)
        familyRepository.save(family)

        return event.id!!
    }

    @Transactional
    override fun deleteEvent(familyId: Int, eventId: Int) {
        val family = find(familyId)

        family.removeEvent(eventId)
        familyRepository.save(family)

        familyEventRepository.deleteById(eventId)
    }

    override fun findEvents(familyId: Int): Collection<FamilyEvent> = familyRepository.findEvents(familyId)

    @Transactional
    override fun createChild(familyId: Int, child: Person): Int {
        val family = find(familyId)

        val saved = personRepository.save(child)
        family.children.add(saved)

        familyRepository.save(family)
        return saved.id!!
    }

    @Transactional
    override fun addChild(familyId: Int, childId: Int) {
        val family = find(familyId)

        val child = personRepository.findById(childId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$childId' is not found") }

        if (family.children.contains(child)) {
            throw ResponseStatusException(INTERNAL_SERVER_ERROR, "Child with id '$childId' already in family '$familyId'")
        }
        family.children.add(child)

        familyRepository.save(family)
    }

    override fun findChildren(familyId: Int): Collection<Person> = find(familyId).children

    @Transactional
    override fun deleteChild(familyId: Int, childId: Int) {
        val family = find(familyId)
        family.children.removeIf { it.id == childId }

        familyRepository.save(family)
    }

    private fun find(familyId: Int): Family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }

    private fun mapFamily(entity: Family): FamilyDTO {
        return mapper.map(entity, FamilyDTO::class.java)
    }
}
