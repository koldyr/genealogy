package com.koldyr.genealogy.model

import java.time.LocalDate
import java.util.function.Predicate
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
@Entity
@Table(name = "T_LIFE_EVENT")
class LifeEvent() : Comparable<LifeEvent?>, Cloneable {

    @Id
    @Column(name = "EVENT_ID")
    var id: Int? = null

    @Enumerated
    var type: EventType = EventType.Birth

    var prefix: EventPrefix? = null

    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonDeserialize(using = LocalDateDeserializer::class)
    var date: LocalDate? = null

    var place: String? = null

    var note: String? = null

    constructor(type: EventType) : this() {
        this.type = type
    }

    constructor(type: EventType, prefix: EventPrefix?, date: LocalDate, place: String?, note: String?) : this() {
        this.type = type
        this.prefix = prefix
        this.date = date
        this.place = place
        this.note = note
    }

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

    public override fun clone(): LifeEvent {
        return super.clone() as LifeEvent
    }
}
