package com.koldyr.genealogy.model

import java.time.LocalDate
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Description of class FamilyEvent
 *
 * @author d.halitski@gmail.com
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
