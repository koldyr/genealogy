package com.koldyr.genealogy.model.converter

import com.koldyr.genealogy.model.EventType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * Description of class EventTypeConverter
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
