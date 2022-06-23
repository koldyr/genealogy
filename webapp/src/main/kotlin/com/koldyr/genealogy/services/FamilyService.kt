package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person

/**
 * Description of class FamilyService
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
interface FamilyService {
    fun findAll(lineageId: Long): List<FamilyDTO>
    fun create(family: FamilyDTO): Long
    fun findById(familyId: Long): FamilyDTO
    fun update(familyId: Long, family: FamilyDTO)
    fun delete(familyId: Long)

    fun createEvent(familyId: Long, event: FamilyEvent): Long
    fun deleteEvent(familyId: Long, eventId: Long)
    fun findEvents(familyId: Long): Collection<FamilyEvent>

    fun createChild(familyId: Long, child: Person): Long
    fun addChild(familyId: Long, childId: Long)
    fun findChildren(familyId: Long): Collection<Person>
    fun deleteChild(familyId: Long, childId: Long)
}
