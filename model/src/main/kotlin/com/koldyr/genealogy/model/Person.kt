package com.koldyr.genealogy.model

import java.util.function.Predicate
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_PERSON")
@JsonPropertyOrder("id", "name", "gender", "place", "occupation", "note", "events", "parentFamily", "family")
class Person(): Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "PERSON_ID")
    var id: Int? = null

    @Embedded
    var name: PersonNames? = null

    @Transient
    var events: MutableSet<LifeEvent> = mutableSetOf()

    var place: String? = null

    var occupation: String? = null

    var note: String? = null

    var gender: Gender = Gender.MALE

    @Column(name = "PARENT_FAMILY_ID", nullable = true)
    var parentFamilyId: Int? = null

    @Column(name = "FAMILY_ID", nullable = true)
    var familyId: Int? = null

    constructor(id: Int) : this() {
        this.id = id
    }

    constructor(id: Int, name: PersonNames, events: MutableSet<LifeEvent>, place: String?, occupation: String?, note: String?, gender: Gender, family: Int?) : this() {
        this.id = id
        this.name = name
        this.events = events
        this.place = place
        this.occupation = occupation
        this.note = note
        this.gender = gender
        this.familyId = family
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id!!
    }

    @JsonIgnore
    fun getBirth(): LifeEvent? {
        return events.firstOrNull { it.type == EventType.Birth }
    }

    @JsonIgnore
    fun getDeath(): LifeEvent? {
        return events.firstOrNull { it.type == EventType.Death }
    }

    fun search(checkFn: Predicate<String?>): Boolean {
        return checkFn.test(note)
                || checkFn.test(occupation)
                || checkFn.test(place)
                || checkFn.test(note)
                || checkFn.test(gender.name)
                || (if (name == null) false else name!!.search(checkFn))
                || checkEvents(checkFn)

    }

    public override fun clone(): Person {
        return super.clone() as Person;
    }

    private fun checkEvents(checkFn: Predicate<String?>): Boolean {
        return events.firstOrNull { it.search(checkFn) } != null
    }
}
