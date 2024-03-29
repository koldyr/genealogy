package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.koldyr.genealogy.model.Person

/**
 * Description of class ChildrenSerializer
 *
 * @author d.halitski@gmail.com
 * @created: 2019-11-05
 */
class ChildrenSerializer : StdSerializer<Set<*>>(Set::class.java) {

    override fun serialize(value: Set<*>?, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()

        if (value != null && value.isNotEmpty()) {
            value.forEach {
                val person: Person = it as Person
                gen.writeRaw('\n')
                gen.writeNumber(person.id!!)
            }
        }

        gen.writeEndArray()
    }
}
