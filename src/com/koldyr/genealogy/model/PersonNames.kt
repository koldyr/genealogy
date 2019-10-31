package com.koldyr.genealogy.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType

/**
 * Description of class PersonNames
 * @created: 2019-10-26
 */
@XmlAccessorType(XmlAccessType.FIELD)
data class PersonNames(var name: String) {
    var middle: String? = null
    var last: String? = null
    var maiden: String? = null

    constructor(): this("")

    constructor(name: String, middle: String?, last: String?, maiden: String?): this(name) {
        this.middle = middle
        this.last = last
        this.maiden = maiden
    }
}
