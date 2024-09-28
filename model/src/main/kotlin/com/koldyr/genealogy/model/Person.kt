package com.koldyr.genealogy.model

import java.util.function.Predicate
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.Basic
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.koldyr.genealogy.model.converter.GenderConverter

/**
 * Description of class Person
 *
 * @author d.halitski@gmail.com
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_PERSON")
@SequenceGenerator(name = "PersonIds", sequenceName = "SEQ_PERSON", allocationSize = 1)
@JsonPropertyOrder("id", "name", "gender", "place", "occupation", "note", "events", "parentFamily", "family")
class Person() : Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PersonIds")
    @Column(name = "PERSON_ID")
    var id: Long? = null

    @Embedded
    @AttributeOverrides(value = [
        AttributeOverride(name = "first", column = Column(name = "FIRST_NAME")),
        AttributeOverride(name = "middle", column = Column(name = "MIDDLE_NAME")),
        AttributeOverride(name = "last", column = Column(name = "LAST_NAME")),
        AttributeOverride(name = "maiden", column = Column(name = "MAIDEN_NAME"))
    ])
    var name: PersonNames? = null

    @OneToMany(mappedBy = "person", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var events: MutableSet<PersonEvent> = mutableSetOf()

    @Size(max = 256)
    var place: String? = null

    @Size(max = 256)
    var occupation: String? = null

    @Size(max = 256)
    var note: String? = null

    @Basic(optional = false)
    @Convert(converter = GenderConverter::class)
    var gender: Gender = Gender.MALE

    @Column(name = "PARENT_FAMILY_ID", nullable = true)
    var parentFamilyId: Long? = null

    @Column(name = "FAMILY_ID", nullable = true)
    var familyId: Long? = null

    @Column(name = "PHOTO_URL", nullable = true)
    @Size(max = 1024)
    var photoUrl: String? = null

    @Lob @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    var photo: ByteArray? = null

    @ManyToOne @JoinColumn(name = "USER_ID", nullable = false) @JsonIgnore
    var user: User? = null

    @Column(name = "LINEAGE_ID") @JsonIgnore
    var lineageId: Long? = null

    constructor(id: Long) : this() {
        this.id = id
    }

    constructor(id: Long, name: PersonNames, events: MutableSet<PersonEvent>, place: String?, occupation: String?, note: String?, gender: Gender, family: Long?) : this() {
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
        return id!!.toInt()
    }

    public override fun clone(): Person {
        return super.clone() as Person;
    }

    override fun toString(): String {
        return "Person(id=$id, name=$name, place=$place, occupation=$occupation, note=$note, gender=$gender)"
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

    private fun checkEvents(checkFn: Predicate<String?>): Boolean {
        return events.firstOrNull { it.search(checkFn) } != null
    }

    fun addEvent(event: PersonEvent) {
        events.add(event)
        event.person = this
    }

    fun removeEvent(eventId: Long) {
        events.find { it.id == eventId }?.also {
            events.remove(it)
            it.person = null
        }
    }
}
