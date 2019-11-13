package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@JsonPropertyOrder("id", "name", "gender", "place", "occupation", "note", "familyId", "events")
data class Person(
        val id: Int,
        var name: PersonNames? = null,
        var events: MutableSet<LifeEvent> = mutableSetOf(),
        var place: String? = null,
        var occupation: String? = null,
        var note: String? = null,
        var gender: Gender = Gender.MALE,
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
        return events.stream()
                .filter { it.type == EventType.Birth }
                .findFirst()
                .orElse(null)
    }

    @JsonIgnore
    fun getDeath(): LifeEvent? {
        return events.stream()
                .filter { it.type == EventType.Death }
                .findFirst()
                .orElse(null)
    }
}
