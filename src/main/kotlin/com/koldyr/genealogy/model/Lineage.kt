package com.koldyr.genealogy.model

/**
 * Description of class Lineage
 * @created: 2019-10-30
 */
data class Lineage(
        var persons: Collection<Person>,
        var families: Set<Family>
) {

    constructor(persons: Collection<Person>, families: Set<Family>, rebuild: Boolean) : this(persons, families) {
        if (rebuild) {
            families.forEach {
                if (it.wife != null) {
                    val person = findPerson(it.wife?.id)
                    if (person != null) {
                        it.wife = person
                    }
                }

                if (it.husband != null) {
                    val person = findPerson(it.husband?.id)
                    if (person != null) {
                        it.husband = person
                    }
                }

                for (child in it.children.toSet()) {
                    val person = findPerson(child.id)
                    if (person != null) {
                        it.children.remove(child)
                        it.children.add(person)
                    }
                }
            }
        }
    }

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
