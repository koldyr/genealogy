package com.koldyr.genealogy.model.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import com.koldyr.genealogy.model.EventPrefix

/**
 * Description of class EventPrefixConverter
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-27
 */
@Converter
class EventPrefixConverter: AttributeConverter<EventPrefix?, String?> {

    override fun convertToDatabaseColumn(type: EventPrefix?): String? {
        return type?.code
    }

    override fun convertToEntityAttribute(type: String?): EventPrefix? {
        return EventPrefix.parse(type)
    }
}
