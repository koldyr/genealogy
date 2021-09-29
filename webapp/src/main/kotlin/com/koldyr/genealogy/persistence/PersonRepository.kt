package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Description of class PersonRepository
 * @created: 2021-09-25
 */
@Repository("personRepository")
interface PersonRepository : JpaRepository<Person, Int> {
    fun findEventsById(eventId: Int): Collection<PersonEvent>
}
