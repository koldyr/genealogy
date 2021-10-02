package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent

/**
 * Description of class FamilyService
 * @created: 2021-09-28
 */
interface FamilyService {
    fun create(family: Family): Int
    fun findAll(): List<FamilyDTO>
    fun findById(familyId: Int): FamilyDTO
    fun update(familyId: Int, family: Family)
    fun delete(familyId: Int)
    fun createEvent(familyId: Int, event: FamilyEvent)
    fun deleteEvent(familyId: Int, eventId: Int)
}
