package com.koldyr.genealogy.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.User

/**
 * Description of the LineageRepository class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
@Repository("lineageRepository")
interface LineageRepository : JpaRepository<Lineage, Long> {
    fun findAllByUser(user: User): Collection<Lineage>
}