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
        var sex: Sex? = null,
        var familyId: Int? = null
)
