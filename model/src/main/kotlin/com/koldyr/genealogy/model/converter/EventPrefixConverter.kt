package com.koldyr.genealogy.model.converter

import com.koldyr.genealogy.model.EventPrefix
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * Description of class EventPrefixConverter
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
