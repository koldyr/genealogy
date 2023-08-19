package com.koldyr.genealogy.model

import java.util.*
import java.util.function.Predicate
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Size

/**
 * Description of class PersonNames
 *
 * @author d.halitski@gmail.com
 * @created: 2019-10-26
 */
@Embeddable
class PersonNames() : Cloneable {
    @Size(max = 256)
    var first: String? = null

    @Size(max = 256)
    var middle: String? = null

    @Size(max = 256)
    var last: String? = null

    @Size(max = 256)
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

    public override fun clone(): PersonNames {
        return super.clone() as PersonNames
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonNames

        if (first != other.first) return false
        if (middle != other.middle) return false
        if (last != other.last) return false
        if (maiden != other.maiden) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (middle?.hashCode() ?: 0)
        result = 31 * result + (last?.hashCode() ?: 0)
        result = 31 * result + (maiden?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "PersonNames(first=$first, last=$last)"
    }

    fun search(checkFn: Predicate<String?>): Boolean {
        return checkFn.test(first) || checkFn.test(middle) || checkFn.test(last) || checkFn.test(maiden)
    }

    fun toUiString(): CharSequence {
        val names = StringJoiner(" ")
            .add(last)
            .add(first)
            .add(middle)
        if (maiden != null) {
            names.add(" ($maiden)")
        }
        return names.toString()
    }
}
