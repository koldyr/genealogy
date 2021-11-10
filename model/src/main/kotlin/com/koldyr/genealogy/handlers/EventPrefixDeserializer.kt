package com.koldyr.genealogy.handlers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.koldyr.genealogy.model.EventPrefix

class EventPrefixDeserializer : StdDeserializer<EventPrefix>(EventPrefix::class.java) {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): EventPrefix? {
        val node: JsonNode = p.codec.readTree(p)
        return EventPrefix.parse(node.asText())
    }
}
 
