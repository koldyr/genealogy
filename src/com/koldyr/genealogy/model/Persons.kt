package com.koldyr.genealogy.model

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * Description of class Persons
 * @created: 2019-10-30
 */
@XmlRootElement
class Persons {

    @XmlElement(name = "person")
    private var persons: Collection<Person>

    constructor() {
        persons = emptyList()
    }

    constructor(persons: Collection<Person>) {
        this.persons = persons
    }
}
