package com.koldyr.genealogy.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.PersonEvent

/**
 * Description of class FamilyEventRepository
 *
 * @author d.halitski@gmail.com
 * @created: 2021-10-08
 */
@Repository("personEventRepository")
interface PersonEventRepository: JpaRepository<PersonEvent, Long> {
    fun findAllByPersonId(id: Long): Collection<PersonEvent>
}
