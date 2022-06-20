package com.koldyr.genealogy.services

import java.io.InputStream
import com.koldyr.genealogy.dto.SearchDTO
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent

/**
 * Description of class PersonService
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
interface PersonService {
    fun findAll(): List<Person>
    fun search(criteria: SearchDTO): Collection<Person>

    fun create(person: Person): Int
    fun findById(personId: Int): Person
    fun update(personId: Int, person: Person)
    fun delete(personId: Int)

    fun createEvent(personId: Int, event: PersonEvent): Int
    fun findEvents(personId: Int): Collection<PersonEvent>
    fun deleteEvent(personId: Int, eventId: Int)
    
    fun photo(personId: Int): InputStream
    fun createPhoto(personId: Int, type: String, photo: ByteArray): String
}
