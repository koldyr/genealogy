package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.koldyr.genealogy.model.converter.EventTypeConverter
import java.time.LocalDate
import java.util.function.Predicate
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.EnumType.*
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id
import javax.persistence.Inheritance
import javax.persistence.InheritanceType.*
import javax.persistence.SequenceGenerator
import javax.persistence.Table

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
@Entity
@Table(name = "T_LIFE_EVENT")
@Inheritance(strategy = JOINED)
@SequenceGenerator(name = "EventIds", sequenceName = "SEQ_EVENT", allocationSize = 1)
open class LifeEvent() : Comparable<LifeEvent?>, Cloneable {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "EventIds")
    @Column(name = "EVENT_ID")
    open var id: Int? = null

    @Basic(optional = false)
    @Convert(converter = EventTypeConverter::class)
    open var type: EventType = EventType.Birth

    @Enumerated(STRING)
    open var prefix: EventPrefix? = null

    @Column(name = "EVENT_DATE", nullable = false, columnDefinition = "DATE")
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonDeserialize(using = LocalDateDeserializer::class)
    open var date: LocalDate? = null

    open var place: String? = null

    open var note: String? = null

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

    fun toPersonEvent(): PersonEvent {
        val event = PersonEvent(type)
        event.id = this.id
        event.date = this.date
        event.prefix = this.prefix
        event.place = this.place
        event.note = this.note
        return event
    }
}
