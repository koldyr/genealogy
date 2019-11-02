package com.koldyr.genealogy.model

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType

/**
 * Description of class PersonNames
 * @created: 2019-10-26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Suppress("unused")
data class PersonNames(
        var name: String,
        var middle: String? = null,
        var last: String? = null,
        var maiden: String? = null
        ) {

    constructor() : this("")
}
