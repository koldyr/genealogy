package com.koldyr.genealogy.model

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Description of class PersonEvent
 * @created: 2021-09-28
 */
@Entity
@Table(name = "T_PERSON_EVENT")
class PersonEvent() : LifeEvent() {

//    @ManyToOne(fetch = LAZY)
//    @JsonIgnore
//    var person: Person? = null

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

    override fun clone(): PersonEvent {
        return super.clone() as PersonEvent
    }
}
