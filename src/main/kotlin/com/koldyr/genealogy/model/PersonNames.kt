package com.koldyr.genealogy.model

import java.util.function.Predicate

/**
 * Description of class PersonNames
 * @created: 2019-10-26
 */
data class PersonNames(
        var name: String?,
        var middle: String? = null,
        var last: String? = null,
        var maiden: String? = null
) {
    fun search(checkFn: Predicate<String?>): Boolean {
        return checkFn.test(name) || checkFn.test(middle) || checkFn.test(last) || checkFn.test(maiden)
    }
}
