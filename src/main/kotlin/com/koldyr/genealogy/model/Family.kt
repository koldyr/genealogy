package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.PersonIdSerializer

/**
 * Description of class Family
 * @created: 2019-10-25
 */
data class Family(var id: Int) {
    val events: MutableSet<LifeEvent> = mutableSetOf()

    @JsonSerialize(using = PersonIdSerializer::class)
    var husband: Person? = null

    @JsonSerialize(using = PersonIdSerializer::class)
    var wife: Person? = null

    @JsonSerialize(using = ChildrenSerializer::class)
    val children: MutableSet<Person> = mutableSetOf()

    var note: String? = null
}
