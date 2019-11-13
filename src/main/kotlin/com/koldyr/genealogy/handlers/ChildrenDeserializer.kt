package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.koldyr.genealogy.model.Person

/**
 * Description of class ChildrenDeserializer
 * @created: 2019-11-05
 */
class ChildrenDeserializer : StdDeserializer<Set<Person>>(Set::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Set<Person> {
        val node: ArrayNode = p.codec.readTree(p)
        val id = node.get("id").asInt()

        return emptySet()
    }
}
