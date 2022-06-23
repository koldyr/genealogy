package com.koldyr.genealogy.model

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Description of class Lineage
 *
 * @author d.halitski@gmail.com
 * @created: 2022-06-23
 */
@Entity
@Table(name = "T_LINEAGE")
@SequenceGenerator(name = "LineageIds", sequenceName = "SEQ_LINEAGE", allocationSize = 1)
class Lineage() {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LineageIds")
    @Column(name = "LINEAGE_ID")
    var id: Long? = null

    @Column(name = "LINEAGE_NAME")
    var name: String? = null

    var note: String? = null

    @OneToMany(fetch = LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JoinColumn(name = "LINEAGE_ID")
    var persons: Set<Person> = setOf()

    @OneToMany(fetch = LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JoinColumn(name = "LINEAGE_ID")
    var families: Set<Family> = setOf()

    @JoinColumn(name = "USER_ID", nullable = false)
    @ManyToOne(fetch = LAZY)
    @JsonIgnore
    var user: User? = null

    constructor(persons: Set<Person>, families: Set<Family>, rebuild: Boolean = false) : this() {
        this.persons = persons
        this.families = families

        if (rebuild) {
            families.forEach {
                if (it.wife != null) {
                    val person = findPerson(it.wife?.id)
                    if (person != null) {
                        it.wife = person
                    }
                }

                if (it.husband != null) {
                    val person = findPerson(it.husband?.id)
                    if (person != null) {
                        it.husband = person
                    }
                }

                for (child in it.children.toSet()) {
                    val person = findPerson(child.id)
                    if (person != null) {
                        it.children.remove(child)
                        it.children.add(person)
                    }
                }
            }
        }
    }

    fun findFamily(id: Long?): Family? {
        return families.stream()
            .filter { it.id == id }
            .findFirst()
            .orElse(null)
    }

    fun findPerson(id: Long?): Person? {
        return persons.stream()
            .filter { it.id == id }
            .findFirst()
            .orElse(null)
    }
}
