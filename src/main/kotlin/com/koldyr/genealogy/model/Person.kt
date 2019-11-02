package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlType

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@JsonPropertyOrder("id", "name", "sex", "birth", "death", "place", "occupation", "note", "familyId")
@XmlType(propOrder = ["id", "name", "sex", "birth", "death", "place", "occupation", "note", "familyId"])
@XmlAccessorType(XmlAccessType.FIELD)
data class Person(
        @field:XmlAttribute var id: Int,
        var name: PersonNames? = null,
        var birth: LifeEvent? = null,
        var death: LifeEvent? = null,
        var place: String? = null,
        var occupation: String? = null,
        var note: String? = null,
        @field:XmlAttribute var sex: Sex = Sex.MALE,
        @field:XmlAttribute var familyId: Int? = null
) {

    constructor() : this(-1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}
