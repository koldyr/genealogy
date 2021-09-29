package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent

/**
 * Description of class GenealogyService
 * @created: 2021-09-28
 */
interface GenealogyService {
    fun create(person: Person): Int
    fun findAllPersons(): List<Person>
    fun findPersonById(personId: Int): Person
    fun update(personId: Int, person: Person)
    fun deletePerson(personId: Int)
    fun createPersonEvent(personId: Int, event: PersonEvent)
    fun deletePersonEvent(personId: Int, eventId: Int)
    fun findAllFamilies(): List<FamilyDTO>
}
