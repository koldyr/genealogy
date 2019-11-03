package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@JsonPropertyOrder("id", "name", "sex", "birth", "death", "place", "occupation", "note", "familyId")
data class Person(
        @JacksonXmlProperty(isAttribute = true) var id: Int,
        var name: PersonNames? = null,
        var birth: LifeEvent? = null,
        var death: LifeEvent? = null,
        var place: String? = null,
        var occupation: String? = null,
        var note: String? = null,
        @JacksonXmlProperty(isAttribute = true) var sex: Sex = Sex.MALE,
        @JacksonXmlProperty(isAttribute = true) var familyId: Int? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}
