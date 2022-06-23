package com.koldyr.genealogy.dto

/**
 * Description of class FamilyDTO
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
class FamilyDTO {
    var id: Long? = null
    var husband: Long? = null
    var wife: Long? = null
    var children: Collection<Long>? = null
    var events: Collection<Long>? = null

    override fun toString(): String {
        return "FamilyDTO(id=$id, husband=$husband, wife=$wife, children=$children, events=$events)"
    }
}
