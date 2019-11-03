package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
data class LifeEvent(
        @JsonSerialize(using = LocalDateSerializer::class)
        @JsonDeserialize(using = LocalDateDeserializer::class)
        var date: LocalDate? = null,
        var place: String? = null,
        var prefix: String? = null
)
