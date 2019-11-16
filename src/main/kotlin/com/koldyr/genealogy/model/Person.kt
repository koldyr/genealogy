package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.function.Predicate

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@JsonPropertyOrder("id", "name", "gender", "place", "occupation", "note", "events", "parentFamily", "family")
data class Person(
        val id: Int,
        var name: PersonNames? = null,
        var events: MutableSet<LifeEvent> = mutableSetOf(),
        var place: String? = null,
        var occupation: String? = null,
        var note: String? = null,
        var gender: Gender = Gender.MALE,
        var parentFamily: Int? = null,
        var family: Int? = null
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
        return events.firstOrNull { it.type == EventType.Birth }
    }

    @JsonIgnore
    fun getDeath(): LifeEvent? {
        return events.firstOrNull { it.type == EventType.Death }
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
}
