package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.koldyr.genealogy.model.Person

/**
 * Description of class PersonSerializer
 * @created: 2019-11-05
 */
class PersonIdDeserializer : StdDeserializer<Person>(Person::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Person? {
        val node: JsonNode = p.codec.readTree(p)
        return Person(node.asInt())
    }
}
