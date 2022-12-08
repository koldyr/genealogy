package com.koldyr.genealogy.model

import java.time.LocalDate
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Description of class PersonEvent
 * @created: 2021-09-28
 */
@Entity
@Table(name = "T_PERSON_EVENT")
class PersonEvent() : LifeEvent() {

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "PERSON_ID")
    @JsonIgnore
    var person: Person? = null

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

    public override fun clone(): PersonEvent {
        return super.clone() as PersonEvent
    }
}
