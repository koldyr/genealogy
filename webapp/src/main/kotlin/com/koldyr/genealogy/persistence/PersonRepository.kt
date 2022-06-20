package com.koldyr.genealogy.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.User

/**
 * Description of class PersonRepository
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-25
 */
@Repository("personRepository")
interface PersonRepository : JpaRepository<Person, Int>, JpaSpecificationExecutor<Person> {
    @Query("select p.events from Person as p where p.id = :personId")
    fun findEvents(@Param("personId") personId: Int): Collection<PersonEvent>

    fun findAllByUser(user : User) : List<Person>
}
