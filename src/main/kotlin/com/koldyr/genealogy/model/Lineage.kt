package com.koldyr.genealogy.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * Description of class Lineage
 * @created: 2019-10-30
 */
@JacksonXmlRootElement
class Lineage(
        @field:JacksonXmlElementWrapper(localName = "persons")
        @field:JsonProperty("persons")
        var person: Collection<Person>,

        @field:JacksonXmlElementWrapper(localName = "families")
        @field:JsonProperty("families")
        var family: Set<Family>
) {

    fun findFamily(id: Int?): Family? {
        return family.stream()
                .filter { it.id == id }
                .findFirst()
                .orElse(null)
    }
}
