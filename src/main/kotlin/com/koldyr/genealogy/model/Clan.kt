package com.koldyr.genealogy.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * Description of class Persons
 * @created: 2019-10-30
 */
@JacksonXmlRootElement
class Clan(var persons: Collection<Person>)
