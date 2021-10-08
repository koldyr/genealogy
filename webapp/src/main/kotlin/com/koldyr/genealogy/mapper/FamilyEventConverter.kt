package com.koldyr.genealogy.mapper

import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.persistence.FamilyEventRepository
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.converter.BidirectionalConverter
import ma.glasnost.orika.metadata.Type
import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException

/**
 * Description of class FamilyEventConverter
 * @created: 2021-10-08
 */
class FamilyEventConverter(private val familyEventRepository: FamilyEventRepository) : BidirectionalConverter<Int, FamilyEvent>() {

    override fun convertTo(eventId: Int?, type: Type<FamilyEvent>?, context: MappingContext?): FamilyEvent? {
        if (eventId == null) {
            return null
        }
        return familyEventRepository.findById(eventId)
                .orElseThrow { ResponseStatusException(NOT_FOUND, "Event with id '$eventId' is not found") }
    }

    override fun convertFrom(event: FamilyEvent?, type: Type<Int>?, context: MappingContext?): Int? {
        return event?.id
    }
}
