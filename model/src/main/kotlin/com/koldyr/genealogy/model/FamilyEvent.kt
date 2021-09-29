package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

/**
 * Description of class FamilyEvent
 * @created: 2021-09-28
 */
@Entity
@Table(name = "T_FAMILY_EVENT")
class FamilyEvent() : LifeEvent() {

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "FAMILY_ID")
    @JsonIgnore
    var family: Family? = null

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

    override fun clone(): FamilyEvent {
        return super.clone() as FamilyEvent
    }
}
