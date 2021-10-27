package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyEventRepository
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository
import ma.glasnost.orika.MapperFacade
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.Objects.isNull
import java.util.Objects.nonNull

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
        family.id = null
        val newFamily = mapper.map(family, Family::class.java)

        if (isNull(newFamily.husband) && isNull(newFamily.wife)) {
            throw ResponseStatusException(BAD_REQUEST, "husband or wife must be is not empty")
        }

        if (nonNull(newFamily.husband)) {
            if (nonNull(newFamily.husband?.familyId)) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.husband}' already in family ${newFamily.husband?.familyId}")
            }
            if (newFamily.husband?.gender == Gender.FEMALE) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.husband}' is woman and can not be husband")
            }
        }

        if (nonNull(newFamily.wife)) {
            if (nonNull(newFamily.wife?.familyId)) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.wife}' already in family ${newFamily.wife?.familyId}")
            }
            if (newFamily.wife?.gender == Gender.MALE) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.wife}' is man and can not be wife")
            }
        }

        familyRepository.save(newFamily)

        if (nonNull(newFamily.husband)) {
            newFamily.husband?.familyId = newFamily.id
            personRepository.save(newFamily.husband!!)
        }
        if (nonNull(newFamily.wife)) {
            newFamily.wife?.familyId = newFamily.id
            personRepository.save(newFamily.wife!!)
        }

        return newFamily.id!!
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

        if (nonNull(persisted.husband)) {
            if (nonNull(persisted.husband?.familyId) && persisted.husband!!.familyId != persisted.id) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.husband}' already in family ${persisted.husband?.familyId}")
            }
            if (persisted.husband?.gender == Gender.FEMALE) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.husband}' is woman and can not be husband")
            }
        }

        if (nonNull(persisted.wife)) {
            if (nonNull(persisted.wife?.familyId) && persisted.wife!!.familyId != persisted.id) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.wife}' already in family ${persisted.wife?.familyId}")
            }
            if (persisted.wife?.gender == Gender.MALE) {
                throw ResponseStatusException(BAD_REQUEST, "Person with id '${family.wife}' is man and can not be wife")
            }
        }

        familyRepository.save(persisted);
    }

    @Transactional
    override fun delete(familyId: Int) {
        val family = find(familyId)

        if (nonNull(family.wife)) {
            family.wife!!.familyId = null
            personRepository.save(family.wife!!)
        }

        if (nonNull(family.husband)) {
            family.husband!!.familyId = null
            personRepository.save(family.husband!!)
        }

        familyRepository.deleteById(familyId)
    }

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

    override fun findEvents(familyId: Int): Collection<FamilyEvent> {
        find(familyId)
        return familyRepository.findEvents(familyId)
    }

    @Transactional
    override fun createChild(familyId: Int, child: Person): Int {
        val family = find(familyId)

        child.id = null
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

        val childFamily = familyRepository.findChild(childId)
        if (childFamily.isPresent) {
            if (childFamily.get().id == familyId) {
                throw ResponseStatusException(BAD_REQUEST, "Child with id '$childId' already in family '$familyId'")
            }
            throw ResponseStatusException(BAD_REQUEST, "Child with id '$childId' already in family '${childFamily.get().id}'")
        }
        family.children.add(child)

        familyRepository.save(family)
    }

    override fun findChildren(familyId: Int): Collection<Person> {
        find(familyId)
        return familyRepository.findChildren(familyId)
    }

    @Transactional
    override fun deleteChild(familyId: Int, childId: Int) {
        val family = find(familyId)
        if (family.children.none { it.id ==childId }) {
            throw ResponseStatusException(BAD_REQUEST, "Child with id '${childId}' is not found in family")
        }
        family.children.removeIf { it.id == childId }

        familyRepository.save(family)
    }

    private fun find(familyId: Int): Family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }

    private fun mapFamily(entity: Family): FamilyDTO {
        return mapper.map(entity, FamilyDTO::class.java)
    }
}
