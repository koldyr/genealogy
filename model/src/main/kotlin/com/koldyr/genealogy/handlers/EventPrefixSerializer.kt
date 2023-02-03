package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.koldyr.genealogy.model.EventPrefix

/**
 * Description of class EventPrefixSerializer
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-29
 */
class EventPrefixSerializer : StdSerializer<EventPrefix>(EventPrefix::class.java) {

    override fun serialize(value: EventPrefix?, gen: JsonGenerator, provider: SerializerProvider) {
        if (value == null) {
            gen.writeNull()
        } else {
            gen.writeString(value.code)
        }
    }
}
