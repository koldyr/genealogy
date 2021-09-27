package com.koldyr.genealogy.model

import java.util.function.Predicate
import javax.persistence.Embeddable

/**
 * Description of class PersonNames
 * @created: 2019-10-26
 */
@Embeddable
class PersonNames(): Cloneable {
    var first: String? = null
    var middle: String? = null
    var last: String? = null
    var maiden: String? = null

    constructor(first: String) : this() {
        this.first = first
    }

    constructor(first: String, middle: String?, last: String?, maiden: String?) : this() {
        this.first = first
        this.middle = middle
        this.last = last
        this.maiden = maiden
    }

    fun search(checkFn: Predicate<String?>): Boolean {
        return checkFn.test(first) || checkFn.test(middle) || checkFn.test(last) || checkFn.test(maiden)
    }

    public override fun clone(): PersonNames {
        return super.clone() as PersonNames
    }
}
