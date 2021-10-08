package com.koldyr.genealogy.dto

/**
 * Description of class FamilyDTO
 * @created: 2021-09-28
 */
class FamilyDTO {
    var id: Int? = null
    var husband: Int? = null
    var wife: Int? = null
    var children: Collection<Int>? = null
    var events: Collection<Int>? = null
}
