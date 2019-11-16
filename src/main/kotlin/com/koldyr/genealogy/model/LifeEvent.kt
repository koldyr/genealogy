package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import java.time.LocalDate
import java.util.function.Predicate

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
data class LifeEvent(
        var type: EventType,

        var prefix: EventPrefix? = null,

        @JsonSerialize(using = LocalDateSerializer::class)
        @JsonDeserialize(using = LocalDateDeserializer::class)
        var date: LocalDate? = null,

        var place: String? = null,
        
        var note: String? = null
) : Comparable<LifeEvent?> {

    override fun compareTo(other: LifeEvent?): Int {
        if (other == null) {
            return 1
        }

        if (date == null) {
            return if (other.date == null) 0 else -1
        }
        if (other.date == null) {
            return 1
        }
        return date!!.compareTo(other.date)
    }

    fun search(checkFn: Predicate<String?>): Boolean {
        return checkFn.test(type.name) || checkFn.test(prefix?.name) || checkFn.test(date?.toString()) || checkFn.test(place) || checkFn.test(note)
    }
}
