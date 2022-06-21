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
    fun findAll(): List<FamilyDTO>
    fun create(family: FamilyDTO): Int
    fun findById(familyId: Int): FamilyDTO
    fun update(familyId: Int, family: FamilyDTO)
    fun delete(familyId: Int)

    fun createEvent(familyId: Int, event: FamilyEvent): Int
    fun deleteEvent(familyId: Int, eventId: Int)
    fun findEvents(familyId: Int): Collection<FamilyEvent>

    fun createChild(familyId: Int, child: Person): Int
    fun addChild(familyId: Int, childId: Int)
    fun findChildren(familyId: Int): Collection<Person>
    fun deleteChild(familyId: Int, childId: Int)
}
