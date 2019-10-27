package com.koldyr.genealogy.model

/**
 * Description of class Family
 * @created: 2019-10-25
 */
class Family(var id: Int) {
    var marriage: LifeEvent? = null
    var husband: Person? = null
    var wife: Person? = null
    val children: MutableSet<Person> = mutableSetOf()
    var note: String? = null
}
