package com.koldyr.genealogy.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * Description of class Persons
 * @created: 2019-10-30
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Persons {

    @XmlElement(name = "person")
    var persons: Collection<Person>

    constructor() {
        persons = mutableListOf()
    }

    constructor(persons: Collection<Person>) {
        this.persons = persons
    }
}
