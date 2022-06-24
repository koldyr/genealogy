package com.koldyr.genealogy.services

import ma.glasnost.orika.MapperFacade
import java.util.Objects.*
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyEventRepository
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonRepository

/**
 * Description of class FamilyServiceImpl
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
@Transactional
class FamilyServiceImpl(
    private val familyRepository: FamilyRepository,
    private val personRepository: PersonRepository,
    private val familyEventRepository: FamilyEventRepository,
    private val mapper: MapperFacade,
    private val userService: UserService
) : FamilyService {

    override fun findAll(lineageId: Long): List<FamilyDTO> {
        return familyRepository.findAllByUserAndLineageId(userService.currentUser(), lineageId).map(this::mapFamily)
    }

    override fun create(lineageId: Long, family: FamilyDTO): Long {
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

        newFamily.lineageId = lineageId
        newFamily.user = userService.currentUser()
        familyRepository.save(newFamily)

        if (nonNull(newFamily.husband)) {
            newFamily.husband?.familyId = newFamily.id
            personRepository.save(newFamily.husband!!)
        }
        if (nonNull(newFamily.wife)) {
            newFamily.wife?.familyId = newFamily.id
            personRepository.save(newFamily.wife!!)
        }

        newFamily.children.forEach { it.parentFamilyId = newFamily.id }
        familyRepository.save(newFamily)

        return newFamily.id!!
    }

    override fun findById(familyId: Long): FamilyDTO {
        return familyRepository.findById(familyId)
            .map(this::mapFamily)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
    }

    override fun update(familyId: Long, family: FamilyDTO) {
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

    override fun delete(familyId: Long) {
        val family = find(familyId)

        if (nonNull(family.wife)) {
            family.wife!!.familyId = null
            personRepository.save(family.wife!!)
            family.wife = null
        }

        if (nonNull(family.husband)) {
            family.husband!!.familyId = null
            personRepository.save(family.husband!!)
            family.husband = null
        }

        val children: MutableIterator<Person> = family.children.iterator()
        while (children.hasNext()) {
            val child = children.next()
            child.parentFamilyId = null
            personRepository.save(child)
            children.remove()
        }

        familyRepository.save(family)
        familyRepository.delete(family)
    }

    override fun createEvent(familyId: Long, event: FamilyEvent): Long {
        val family = find(familyId)
        
        event.id = null
        family.addEvent(event)

        familyEventRepository.save(event)
        familyRepository.save(family)

        return event.id!!
    }

    override fun deleteEvent(familyId: Long, eventId: Long) {
        val family = find(familyId)

        family.removeEvent(eventId)
        familyRepository.save(family)

        familyEventRepository.deleteById(eventId)
    }

    override fun findEvents(familyId: Long): Collection<FamilyEvent> {
        find(familyId)
        return familyRepository.findEvents(familyId)
    }

    override fun createChild(familyId: Long, child: Person): Long {
        val family = find(familyId)

        child.id = null
        child.user = userService.currentUser()
        child.familyId = null
        child.parentFamilyId = familyId
        val saved = personRepository.save(child)

        family.addChild(child)
        familyRepository.save(family)
        return saved.id!!
    }

    override fun addChild(familyId: Long, childId: Long) {
        val family = find(familyId)

        val child = personRepository.findById(childId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Person with id '$childId' is not found") }

        val childFamily = familyRepository.findChildFamily(childId)
        if (childFamily.isPresent) {
            if (childFamily.get().id == familyId) {
                throw ResponseStatusException(BAD_REQUEST, "Child with id '$childId' already in family '$familyId'")
            }
            throw ResponseStatusException(BAD_REQUEST, "Child with id '$childId' already in family '${childFamily.get().id}'")
        }

        personRepository.save(child)

        family.addChild(child)
        familyRepository.save(family)
    }

    override fun findChildren(familyId: Long): Collection<Person> {
        find(familyId)
        return familyRepository.findChildren(familyId)
    }

    override fun deleteChild(familyId: Long, childId: Long) {
        val family = find(familyId)
        if (family.children.none { it.id ==childId }) {
            throw ResponseStatusException(BAD_REQUEST, "Child with id '${childId}' is not found in family")
        }
        family.children.removeIf { it.id == childId }

        familyRepository.save(family)
    }

    private fun find(familyId: Long): Family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }

    private fun mapFamily(entity: Family): FamilyDTO {
        return mapper.map(entity, FamilyDTO::class.java)
    }
}
