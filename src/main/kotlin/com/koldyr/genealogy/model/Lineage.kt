package com.koldyr.genealogy.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * Description of class Lineage
 * @created: 2019-10-30
 */
@JacksonXmlRootElement
class Lineage(
        var persons: Collection<Person>,
        var families: Set<Family>
)
