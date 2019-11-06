package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.koldyr.genealogy.model.Person

/**
 * Description of class PersonSerializer
 * @created: 2019-11-05
 */
class PersonIdSerializer : StdSerializer<Person>(Person::class.java) {

    override fun serialize(value: Person?, gen: JsonGenerator, provider: SerializerProvider) {
        if (value == null) {
            gen.writeNull()
        }
        gen.writeNumber(value!!.id)
    }
}
