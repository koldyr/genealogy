package com.koldyr.genealogy.dto

import com.koldyr.genealogy.model.Family

/**
 * Description of class FamilyDTO
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
data class FamilyDTO(
    var id: Long? = null,
    var husband: Long? = null,
    var wife: Long? = null,
    var children: Collection<Long>? = null,
    var events: Collection<Long>? = null
) {

    constructor(family: Family) : this(
        family.id,
        family.husband?.id,
        family.wife?.id,
        family.children.map { it.id!! }.toSet()
    )

    override fun toString(): String {
        return "FamilyDTO(id=$id, husband=$husband, wife=$wife, children=$children, events=$events)"
    }
}
