package com.koldyr.genealogy.model

/**
 * Description of class PersonNames
 * @created: 2019-10-26
 */
data class PersonNames(
        var name: String?,
        var middle: String? = null,
        var last: String? = null,
        var maiden: String? = null
)
