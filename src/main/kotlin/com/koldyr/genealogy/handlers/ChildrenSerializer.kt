package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.koldyr.genealogy.model.Person

/**
 * Description of class PersonSerializer
 * @created: 2019-11-05
 */
class ChildrenSerializer : StdSerializer<Set<*>>(Set::class.java) {
    override fun serialize(value: Set<*>?, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartArray()

        if (value != null && value.isNotEmpty()) {
            if (gen is ToXmlGenerator) {
                val staxWriter = gen.getStaxWriter()
                value.forEach {
                    val person: Person = it as Person
                    staxWriter.writeStartElement("person")
                    staxWriter.writeAttribute("id", person.id.toString())
                    staxWriter.writeEndElement()
                }
            } else {
                value.forEach {
                    val person: Person = it as Person
                    gen.writeNumber(person.id)
                }
            }
        }

        gen.writeEndArray()
    }
}
