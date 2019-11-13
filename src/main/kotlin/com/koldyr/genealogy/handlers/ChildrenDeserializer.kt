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
        val children = mutableSetOf<Person>()

        val node: ArrayNode = p.codec.readTree(p)
        node.elements().forEach {
            children.add(Person(it.asInt()))
        }

        return children
    }
}
