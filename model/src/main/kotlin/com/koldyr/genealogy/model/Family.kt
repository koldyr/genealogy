package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.EventTypeDeserializer
import com.koldyr.genealogy.handlers.PersonIdDeserializer
import com.koldyr.genealogy.handlers.PersonIdSerializer
import javax.persistence.CascadeType.ALL
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.SEQUENCE
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

/**
 * Description of class Family
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_FAMILY")
@SequenceGenerator(name = "FamilyIds", sequenceName = "SEQ_FAMILY", allocationSize = 1)
class Family() {

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "FamilyIds")
    @Column(name = "FAMILY_ID")
    var id: Int? = null

    @OneToOne
    @JoinColumn(name = "HUSBAND_ID", referencedColumnName = "PERSON_ID")
    @JsonSerialize(using = PersonIdSerializer::class)
    @JsonDeserialize(using = PersonIdDeserializer::class)
    var husband: Person? = null

    @OneToOne
    @JoinColumn(name = "WIFE_ID", referencedColumnName = "PERSON_ID")
    @JsonSerialize(using = PersonIdSerializer::class)
    @JsonDeserialize(using = PersonIdDeserializer::class)
    var wife: Person? = null

    @OneToMany(targetEntity = Person::class, cascade = [PERSIST, MERGE, REFRESH])
    @JsonSerialize(using = ChildrenSerializer::class)
    @JsonDeserialize(using = EventTypeDeserializer::class)
    val children: MutableSet<Person> = mutableSetOf()

    @OneToMany(mappedBy = "family",cascade = [ALL], fetch = EAGER, orphanRemoval = true)
    val events: MutableSet<FamilyEvent> = mutableSetOf()

    var note: String? = null

    constructor(id: Int) : this() {
        this.id = id
    }

    fun addEvent(event: FamilyEvent) {
        events.add(event)
        event.family = this
    }

    fun removeEvent(eventId: Int) {
        val event = events.find { it.id == eventId }
        if (event != null) {
            events.remove(event)
            event.family = null
        }
    }
}
