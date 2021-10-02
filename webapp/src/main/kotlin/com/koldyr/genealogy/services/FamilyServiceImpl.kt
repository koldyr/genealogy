package com.koldyr.genealogy.services

import java.util.stream.Collectors.toList
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyRepository
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class FamilyServiceImpl
 * @created: 2021-09-28
 */
open class FamilyServiceImpl(
    private val familyRepository: FamilyRepository
) : FamilyService {

    @Transactional
    override fun create(family: Family): Int {
        val saved = familyRepository.save(family)
        return saved.id!!
    }

    override fun findAll(): List<FamilyDTO> {
        return familyRepository.findAll().stream()
            .map(this::mapFamily)
            .collect(toList())
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

//        persisted.name = person.name
//        persisted.note = person.note
//        persisted.occupation = person.occupation
//        persisted.place = person.place

        familyRepository.save(persisted);
    }

    @Transactional
    override fun delete(familyId: Int) {
        familyRepository.deleteById(familyId)
    }

    @Transactional
    override fun createEvent(familyId: Int, event: FamilyEvent) {
        val family: Family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
        family.addEvent(event)

        val saved = familyRepository.save(family)
    }

    @Transactional
    override fun deleteEvent(familyId: Int, eventId: Int) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Family with id '$familyId' is not found") }
        family.removeEvent(eventId)
        familyRepository.save(family)
    }

    private fun mapFamily(entity: Family): FamilyDTO {
        val dto = FamilyDTO(entity.id!!)
        dto.husband = entity.husband?.id
        dto.wife = entity.wife?.id

        val children: MutableList<Int> = entity.children.stream().map(Person::id).collect(toList())
        dto.children = children.toList()

        return dto
    }
}
