package com.koldyr.genealogy.model

/**
 * Description of class Person
 *
 * @created: 2019-10-25
 */

data class Person(
        var id: Int,
        var name: PersonNames? = null,
        var birth: LifeEvent? = null,
        var death: LifeEvent? = null,
        var place: String? = null,
        var occupation: String? = null,
        var note: String? = null,
        var sex: Sex = Sex.MALE,
        var familyId: Int? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Person) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}
