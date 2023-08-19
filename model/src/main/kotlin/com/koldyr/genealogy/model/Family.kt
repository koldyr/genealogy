package com.koldyr.genealogy.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.koldyr.genealogy.handlers.ChildrenDeserializer
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.PersonIdDeserializer
import com.koldyr.genealogy.handlers.PersonIdSerializer

/**
 * Description of class Family
 *
 * @author d.halitski@gmail.com
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_FAMILY")
@SequenceGenerator(name = "FamilyIds", sequenceName = "SEQ_FAMILY", allocationSize = 1)
class Family() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FamilyIds")
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

    @OneToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH], orphanRemoval = true)
    @JoinTable(
            name = "T_CHILDREN",
            joinColumns = [JoinColumn(name = "family_id", referencedColumnName = "family_id")],
            inverseJoinColumns = [JoinColumn(name = "person_id", referencedColumnName = "person_id")]
    )
    @JsonSerialize(using = ChildrenSerializer::class)
    @JsonDeserialize(using = ChildrenDeserializer::class)
    val children: MutableSet<Person> = mutableSetOf()

    @OneToMany(mappedBy = "family",cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    val events: MutableSet<FamilyEvent> = mutableSetOf()

    @Column(name = "LINEAGE_ID")
    var lineageId: Long? = null

    @ManyToOne @JoinColumn(name = "USER_ID", nullable = false) @JsonIgnore
    lateinit var user: User

    @Size(max = 256)
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
