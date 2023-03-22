package com.koldyr.genealogy.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.FamilyEvent

/**
 * Description of class FamilyEventRepository
 * @created: 2021-10-08
 */
@Repository("familyEventRepository")
interface FamilyEventRepository: JpaRepository<FamilyEvent, Long> {
    fun findAllByFamilyId(id: Long): Collection<FamilyEvent>
}
