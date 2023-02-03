package com.koldyr.genealogy.model.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import com.koldyr.genealogy.model.EventType

/**
 * Description of class EventTypeConverter
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-27
 */
@Converter
class EventTypeConverter: AttributeConverter<EventType, String> {

    override fun convertToDatabaseColumn(type: EventType): String {
        return type.getCode()
    }

    override fun convertToEntityAttribute(type: String): EventType {
        return EventType.parse(type)
    }
}
