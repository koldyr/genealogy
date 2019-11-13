package com.koldyr.genealogy.model

/**
 * Description of class Lineage
 * @created: 2019-10-30
 */
data class Lineage(
        var persons: Collection<Person>,
        var families: Set<Family>
) {

    fun findFamily(id: Int?): Family? {
        return families.stream()
                .filter { it.id == id }
                .findFirst()
                .orElse(null)
    }

    fun findPerson(id: Int?): Person? {
        return persons.stream()
                .filter { it.id == id }
                .findFirst()
                .orElse(null)
    }
}
