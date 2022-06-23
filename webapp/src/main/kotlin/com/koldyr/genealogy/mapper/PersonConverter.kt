package com.koldyr.genealogy.mapper

import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.PersonRepository

/**
 * Description of class PersonConverter
 *
 * @author d.halitski@gmail.com
 * @created: 2021-10-08
 */
class PersonConverter(private val personRepository: PersonRepository) : BidirectionalConverter<Long, Person>() {

    override fun convertTo(personId: Long?, type: Type<Person>?, context: MappingContext?): Person? {
        if (personId == null) {
            return null
        }
        return personRepository.findById(personId)
                .orElseThrow { ResponseStatusException(BAD_REQUEST, "Person with id '$personId' is not found") }
    }

    override fun convertFrom(person: Person?, type: Type<Long>?, context: MappingContext?): Long? {
        return person?.id
    }
}
