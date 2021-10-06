package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.koldyr.genealogy.model.EventType

/**
 * Description of class EventTypeDeserializer
 * @created: 2021-09-29
 */
class EventTypeDeserializer : StdDeserializer<EventType>(EventType::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): EventType {
        val node: JsonNode = p.codec.readTree(p)
        return EventType.parse(node.asText())
    }
}
