package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.koldyr.genealogy.handlers.ChildrenDeserializer
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.PersonIdDeserializer
import com.koldyr.genealogy.handlers.PersonIdSerializer
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient

/**
 * Description of class Family
 * @created: 2019-10-25
 */
@Entity
@Table(name = "T_FAMILY")
class Family() {

    @Id
    @Column(name = "FAMILY_ID")
    var id: Int? = null

    @OneToOne
    @JsonSerialize(using = PersonIdSerializer::class)
    @JsonDeserialize(using = PersonIdDeserializer::class)
    var husband: Person? = null

    @OneToOne
    @JsonSerialize(using = PersonIdSerializer::class)
    @JsonDeserialize(using = PersonIdDeserializer::class)
    var wife: Person? = null

    @Transient
    @JsonSerialize(using = ChildrenSerializer::class)
    @JsonDeserialize(using = ChildrenDeserializer::class)
    val children: MutableSet<Person> = mutableSetOf()

    @Transient
    val events: MutableSet<LifeEvent> = mutableSetOf()

    var note: String? = null

    constructor(id: Int) : this() {
        this.id = id
    }
}
