package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent

/**
 * Description of class PersonService
 * @created: 2021-09-28
 */
interface PersonService {
    fun create(person: Person): Int
    fun findAll(): List<Person>
    fun findById(personId: Int): Person
    fun update(personId: Int, person: Person)
    fun delete(personId: Int)
    fun createEvent(personId: Int, event: PersonEvent): Int
    fun findEvents(personId: Int): Collection<PersonEvent>
    fun deleteEvent(personId: Int, eventId: Int)
}
