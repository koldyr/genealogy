package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@JsonPropertyOrder("id", "name", "sex", "place", "occupation", "note", "familyId", "events")
@JacksonXmlRootElement(localName = "person")
data class Person(
        @field:JacksonXmlProperty(isAttribute = true) var id: Int,
        var name: PersonNames? = null,

        @field:JacksonXmlElementWrapper(localName = "events")
        @JsonProperty("events")
        var event: MutableSet<LifeEvent> = mutableSetOf(),

        var place: String? = null,

        var occupation: String? = null,

        @field:JacksonXmlCData
        var note: String? = null,

        @field:JacksonXmlProperty(isAttribute = true)
        var sex: Sex = Sex.MALE,

        @field:JacksonXmlProperty(isAttribute = true)
        var familyId: Int? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    @JsonIgnore
    fun getBirth(): LifeEvent? {
        return event.stream()
                .filter { it.type == EventType.Birth }
                .findFirst()
                .orElse(null)
    }

    @JsonIgnore
    fun getDeath(): LifeEvent? {
        return event.stream()
                .filter { it.type == EventType.Death }
                .findFirst()
                .orElse(null)
    }
}
