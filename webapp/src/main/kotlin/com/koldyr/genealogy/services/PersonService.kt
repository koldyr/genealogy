package com.koldyr.genealogy.services

import java.io.InputStream
import com.koldyr.genealogy.dto.PageResultDTO
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
    fun findAll(lineageId: Long): List<Person>
    fun search(criteria: SearchDTO): PageResultDTO<Person>

    fun create(person: Person): Long
    fun findById(personId: Long): Person
    fun update(personId: Long, person: Person)
    fun delete(personId: Long)

    fun createEvent(personId: Long, event: PersonEvent): Long
    fun findEvents(personId: Long): Collection<PersonEvent>
    fun deleteEvent(personId: Long, eventId: Long)
    
    fun photo(personId: Long): InputStream
    fun createPhoto(personId: Long, type: String, photo: ByteArray): String
}
