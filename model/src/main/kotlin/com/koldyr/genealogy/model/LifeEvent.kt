package com.koldyr.genealogy.model

import java.time.LocalDate
import java.util.function.Predicate
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.SequenceGenerator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.koldyr.genealogy.handlers.EventPrefixDeserializer
import com.koldyr.genealogy.handlers.EventPrefixSerializer
import com.koldyr.genealogy.handlers.EventTypeDeserializer
import com.koldyr.genealogy.handlers.EventTypeSerializer
import com.koldyr.genealogy.model.converter.EventPrefixConverter
import com.koldyr.genealogy.model.converter.EventTypeConverter

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
@MappedSuperclass
open class LifeEvent() : Comparable<LifeEvent?>, Cloneable {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "SEQ_EVENT")
    @SequenceGenerator(name = "SEQ_EVENT", sequenceName = "SEQ_EVENT", allocationSize = 1)
    @Column(name = "EVENT_ID")
    open var id: Long? = null

    @Basic(optional = false)
    @Convert(converter = EventTypeConverter::class)
    @JsonSerialize(using = EventTypeSerializer::class)
    @JsonDeserialize(using = EventTypeDeserializer::class)
    open var type: EventType = EventType.Birth

    @Convert(converter = EventPrefixConverter::class)
    @JsonSerialize(using = EventPrefixSerializer::class)
    @JsonDeserialize(using = EventPrefixDeserializer::class)
    open var prefix: EventPrefix? = null

    @Column(name = "EVENT_DATE", columnDefinition = "DATE")
    @JsonSerialize(using = LocalDateSerializer::class)
    @JsonDeserialize(using = LocalDateDeserializer::class)
    open var date: LocalDate? = null

    open var place: String? = null

    open var note: String? = null

    constructor(type: EventType) : this() {
        this.type = type
    }

    constructor(type: EventType, prefix: EventPrefix?, date: LocalDate?, place: String?, note: String?) : this() {
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

    fun toFamilyEvent(): FamilyEvent {
        val event = FamilyEvent(type)
        event.id = this.id
        event.date = this.date
        event.prefix = this.prefix
        event.place = this.place
        event.note = this.note
        return event
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LifeEvent

        if (id != other.id) return false
        if (type != other.type) return false
        if (prefix != other.prefix) return false
        if (date != other.date) return false
        if (place != other.place) return false
        if (note != other.note) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (prefix?.hashCode() ?: 0)
        result = 31 * result + (date?.hashCode() ?: 0)
        result = 31 * result + (place?.hashCode() ?: 0)
        result = 31 * result + (note?.hashCode() ?: 0)
        return result.toInt()
    }

    override fun toString(): String {
        return "LifeEvent(id=$id, type=$type, prefix=$prefix, date=$date, place=$place)"
    }
}
