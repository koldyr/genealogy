package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.Family
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class FamilyRepository
 * @created: 2021-09-25
 */
@Repository("familyRepository")
interface FamilyRepository : JpaRepository<Family, Int>
