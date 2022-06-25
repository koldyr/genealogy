package com.koldyr.genealogy.model

import javax.persistence.CascadeType.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.*
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.koldyr.genealogy.handlers.ChildrenDeserializer
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.PersonIdDeserializer
import com.koldyr.genealogy.handlers.PersonIdSerializer

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
    var id: Long? = null

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

    @OneToMany(cascade = [PERSIST, MERGE, REFRESH])
    @JoinTable(
            name = "T_CHILDREN",
            joinColumns = [JoinColumn(name = "family_id", referencedColumnName = "family_id")],
            inverseJoinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")]
    )
    @JsonSerialize(using = ChildrenSerializer::class)
    @JsonDeserialize(using = ChildrenDeserializer::class)
    val children: MutableSet<Person> = mutableSetOf()

    @OneToMany(mappedBy = "family",cascade = [ALL], fetch = EAGER, orphanRemoval = true)
    val events: MutableSet<FamilyEvent> = mutableSetOf()

    @Column(name = "LINEAGE_ID")
    var lineageId: Long? = null

    @JoinColumn(name = "USER_ID", nullable = false)
    @ManyToOne
    @JsonIgnore
    lateinit var user: User

    var note: String? = null

    constructor(id: Long) : this() {
        this.id = id
    }

    fun addChild(child: Person) {
        children.add(child)
        child.parentFamilyId = id
    }

    fun addEvent(event: FamilyEvent) {
        events.add(event)
        event.family = this
    }

    fun removeEvent(eventId: Long) {
        events.find { it.id == eventId }?.let { event ->
            events.remove(event)
            event.family = null
        }
    }

    fun removePerson(person: Person) {
        if (husband != null && husband!! == person) {
            husband = null
        } else if (wife != null && wife!! == person) {
            wife = null
        } else {
            children.remove(person)
        }
    }

    override fun toString(): String {
        return "Family(id=$id, husband=$husband, wife=$wife, children=${children.size}, note=$note)"
    }
}
