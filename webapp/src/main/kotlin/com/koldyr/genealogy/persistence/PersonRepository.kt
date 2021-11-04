package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Description of class PersonRepository
 * @created: 2021-09-25
 */
@Repository("personRepository")
interface PersonRepository : JpaRepository<Person, Int> {
    @Query("select p.events from Person as p where p.id = :personId")
    fun findEvents(@Param("personId") personId: Int): Collection<PersonEvent>

    fun findAllByUser(user : User) : List<Person>
}
