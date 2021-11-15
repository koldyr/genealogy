package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Description of class FamilyRepository
 * @created: 2021-09-25
 */
@Repository("familyRepository")
interface FamilyRepository : JpaRepository<Family, Int> {
    @Query("select f.events from Family as f where f.id = :familyId")
    fun findEvents(@Param("familyId") familyId: Int): Collection<FamilyEvent>

    @Query("select f from Family f join f.children c where c.id = :childId")
    fun findChildFamily(@Param("childId") childId: Int): Optional<Family>

    @Query("select f.children from Family f where f.id = :familyId")
    fun findChildren(@Param("familyId") familyId: Int): Collection<Person>

    fun findAllByUser(user: User): Collection<Family>
}
