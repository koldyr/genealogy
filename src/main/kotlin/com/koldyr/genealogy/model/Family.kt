package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.koldyr.genealogy.handlers.ChildrenSerializer
import com.koldyr.genealogy.handlers.PersonIdSerializer

/**
 * Description of class Family
 * @created: 2019-10-25
 */
@JacksonXmlRootElement(localName = "family")
data class Family(@field:JacksonXmlProperty(isAttribute = true) var id: Int) {
    @JacksonXmlElementWrapper(localName = "events")
    @JsonProperty("events")
    val event: MutableSet<LifeEvent> = mutableSetOf()

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
