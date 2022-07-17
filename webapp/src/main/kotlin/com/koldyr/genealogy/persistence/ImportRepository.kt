package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person

/**
 * Description of class ImportRepository
 *
 * @author d.halitski@gmail.com
 * @created: 2022-07-17
 */
interface ImportRepository {
    fun save(person: Person)
    fun savePersons(persons: Collection<Person>)
    fun save(family: Family)
    fun saveFamilies(families: Collection<Family>)
}