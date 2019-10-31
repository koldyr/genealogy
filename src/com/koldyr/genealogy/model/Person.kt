package com.koldyr.genealogy.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlAttribute

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */
@XmlAccessorType(XmlAccessType.FIELD)
data class Person(@XmlAttribute var id: Int) {

    var name: PersonNames? = null
    var birth: LifeEvent? = null
    var death: LifeEvent? = null
    var place: String? = null
    var occupation: String? = null
    var note: String? = null
    @XmlAttribute var sex: Sex = Sex.MALE
    @XmlAttribute var familyId: Int? = null

    constructor() : this(-1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}
