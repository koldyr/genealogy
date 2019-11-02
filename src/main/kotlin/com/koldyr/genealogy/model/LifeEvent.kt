package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.koldyr.genealogy.importer.LocalDateAdapter
import java.time.LocalDate
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
@XmlAccessorType(XmlAccessType.FIELD)
data class LifeEvent(
        @JsonSerialize(using = LocalDateSerializer::class)
        @JsonDeserialize(using = LocalDateDeserializer::class)
        @XmlJavaTypeAdapter(value = LocalDateAdapter::class)
        var date: LocalDate? = null,
        var place: String? = null
) {
    constructor(): this(null, null)
}
