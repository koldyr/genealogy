package com.koldyr.genealogy.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.PersonIdSerializer

/**
 * Description of class Family
 * @created: 2019-10-25
 */
class Family(@field:JacksonXmlProperty(isAttribute = true) var id: Int) {
    var marriage: LifeEvent? = null

    @field:JacksonXmlProperty(isAttribute = true)
    @JsonSerialize(using = PersonIdSerializer::class)
    var husband: Person? = null

    @field:JacksonXmlProperty(isAttribute = true)
    @JsonSerialize(using = PersonIdSerializer::class)
    var wife: Person? = null

    @JsonSerialize(using = ChildrenSerializer::class)
    val children: MutableSet<Person> = mutableSetOf()

    @JacksonXmlCData
    var note: String? = null
}
