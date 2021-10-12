package com.koldyr.genealogy.mapper

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.PersonRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class PersonConverter
 * @created: 2021-10-08
 */
class PersonConverter(private val personRepository: PersonRepository) : BidirectionalConverter<Int, Person>() {

    override fun convertTo(personId: Int?, type: Type<Person>?, context: MappingContext?): Person? {
        if (personId == null) {
            return null
        }
        return personRepository.findById(personId)
                .orElseThrow { ResponseStatusException(BAD_REQUEST, "Person with id '$personId' is not found") }
    }

    override fun convertFrom(person: Person?, type: Type<Int>?, context: MappingContext?): Int? {
        return person?.id
    }
}
