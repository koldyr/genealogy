package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.FamilyEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class FamilyEventRepository
 * @created: 2021-10-08
 */
@Repository("familyEventRepository")
interface FamilyEventRepository: JpaRepository<FamilyEvent, Int>
