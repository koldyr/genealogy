package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
@JacksonXmlRootElement(localName = "event")
data class LifeEvent(
        @field:JacksonXmlProperty(isAttribute = true)
        var type: EventType,

        @field:JacksonXmlProperty(isAttribute = true)
        var prefix: EventPrefix? = null,

        @field:JsonSerialize(using = LocalDateSerializer::class)
        @field:JsonDeserialize(using = LocalDateDeserializer::class)
        var date: LocalDate? = null,

        var place: String? = null
)
