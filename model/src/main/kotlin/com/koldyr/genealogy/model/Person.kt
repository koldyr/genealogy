package com.koldyr.genealogy.model

import java.util.function.Predicate
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Basic
import javax.persistence.CascadeType.*
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.koldyr.genealogy.model.converter.GenderConverter

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_PERSON")
@SequenceGenerator(name = "PersonIds", sequenceName = "SEQ_PERSON", allocationSize = 1)
@JsonPropertyOrder("id", "name", "gender", "place", "occupation", "note", "events", "parentFamily", "family")
class Person() : Cloneable {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "PersonIds")
    @Column(name = "PERSON_ID")
    var id: Int? = null

    @Embedded
    @AttributeOverrides(value = [
        AttributeOverride(name = "first", column = Column(name = "FIRST_NAME")),
        AttributeOverride(name = "middle", column = Column(name = "MIDDLE_NAME")),
        AttributeOverride(name = "last", column = Column(name = "LAST_NAME")),
        AttributeOverride(name = "maiden", column = Column(name = "MAIDEN_NAME"))
    ])
    var name: PersonNames? = null

    @OneToMany(mappedBy = "person", cascade = [ALL], fetch = EAGER, orphanRemoval = true)
    var events: MutableSet<PersonEvent> = mutableSetOf()

    var place: String? = null

    var occupation: String? = null

    var note: String? = null

    @Basic(optional = false)
    @Convert(converter = GenderConverter::class)
    var gender: Gender = Gender.MALE

    @Column(name = "PARENT_FAMILY_ID", nullable = true)
    var parentFamilyId: Int? = null

    @Column(name = "FAMILY_ID", nullable = true)
    var familyId: Int? = null

    @Column(name = "PHOTO_URL", nullable = true)
    var photoUrl: String? = null

    @Lob @Basic(fetch = LAZY)
    @JsonIgnore
    var photo: ByteArray? = null

    @JoinColumn(name = "USER_ID", nullable = false)
    @ManyToOne
    @JsonIgnore
    var user: User? = null

    constructor(id: Int) : this() {
        this.id = id
    }

    constructor(id: Int, name: PersonNames, events: MutableSet<PersonEvent>, place: String?, occupation: String?, note: String?, gender: Gender, family: Int?) : this() {
        this.id = id
        this.name = name
        this.events = events
        this.place = place
        this.occupation = occupation
        this.note = note
        this.gender = gender
        this.familyId = family
    }

    fun findEvent(type: EventType): LifeEvent? {
        return events.firstOrNull { it.type == type }
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id!!
    }

    public override fun clone(): Person {
        return super.clone() as Person;
    }

    private fun checkEvents(checkFn: Predicate<String?>): Boolean {
        return events.firstOrNull { it.search(checkFn) } != null
    }

    fun addEvent(event: PersonEvent) {
        events.add(event)
        event.person = this
    }

    fun removeEvent(eventId: Int) {
        val event = events.find { it.id == eventId }
        if (event != null) {
            events.remove(event)
            event.person = null
        }
    }

    override fun toString(): String {
        return "Person(id=$id, name=$name, place=$place, occupation=$occupation, note=$note, gender=$gender)"
    }
}
