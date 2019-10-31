package com.koldyr.genealogy.model

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
        @XmlJavaTypeAdapter(type = LocalDate::class, value = LocalDateAdapter::class)
        var date: LocalDate? = null,
        var place: String? = null
) {
    constructor(): this(null, null)
}
