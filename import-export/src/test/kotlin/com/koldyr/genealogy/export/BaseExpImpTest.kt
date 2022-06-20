package com.koldyr.genealogy.export

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import com.koldyr.genealogy.importer.Importer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * Description of class BaseExpImpTest
 * @created: 2019-11-12
 */
abstract class BaseExpImpTest {

    @Test
    fun exportImport() {
        val output = ByteArrayOutputStream()

        val lineage = createLineage()
        getExporter().export(lineage, output)

        println(String(output.toByteArray(), Charsets.UTF_8))

        val input = ByteArrayInputStream(output.toByteArray())
        val result = getImporter().import(input)

        lineage.families.forEach {
            val family = result.findFamily(it.id)
            Assertions.assertEquals(it.id, family!!.id)
            assertPerson(it.husband, family.husband)
            assertPerson(it.wife, family.wife)
            Assertions.assertEquals(it.events, family.events)
            Assertions.assertEquals(it.children, family.children)
            Assertions.assertEquals(it.note, family.note)
        }

        lineage.persons.forEach {
            val person = result.findPerson(it.id)
            Assertions.assertNotNull(person)
            assertPerson(it, person)
        }
    }

    protected abstract fun getImporter(): Importer

    protected abstract fun getExporter(): Exporter

    private fun createLineage(): com.koldyr.genealogy.model.Lineage {
        val events1 = mutableSetOf(
            com.koldyr.genealogy.model.PersonEvent(com.koldyr.genealogy.model.EventType.Birth, null, LocalDate.of(1960, 10, 10), "place11", null),
            com.koldyr.genealogy.model.PersonEvent(
                com.koldyr.genealogy.model.EventType.Education,
                com.koldyr.genealogy.model.EventPrefix.After,
                LocalDate.of(1970, 1, 1),
                "place12",
                null
            )
        )
        val name1 = com.koldyr.genealogy.model.PersonNames("p1_name", "p1_middle", "p1_last", "p1_maiden")
        val person1 = com.koldyr.genealogy.model.Person(1, name1, events1, "place1", "occupation1", "person1\nnote", com.koldyr.genealogy.model.Gender.FEMALE, 1)

        val events2 = mutableSetOf(
            com.koldyr.genealogy.model.PersonEvent(com.koldyr.genealogy.model.EventType.Birth, null, LocalDate.of(1970, 10, 10), "place21", "person2 birth\nnotes"),
            com.koldyr.genealogy.model.PersonEvent(
                com.koldyr.genealogy.model.EventType.Christening,
                com.koldyr.genealogy.model.EventPrefix.About,
                LocalDate.of(1980, 1, 1),
                "place22",
                null
            )
        )
        val name2 = com.koldyr.genealogy.model.PersonNames("p2_name", "p2_middle", "p2_last", null)
        val person2 = com.koldyr.genealogy.model.Person(2, name2, events2, "place2", "occupation2", "note2\nnote2n", com.koldyr.genealogy.model.Gender.MALE, 1)

        val events3 = mutableSetOf(
            com.koldyr.genealogy.model.PersonEvent(com.koldyr.genealogy.model.EventType.Birth, null, LocalDate.of(1995, 10, 10), "place21", null)
        )
        val name3 = com.koldyr.genealogy.model.PersonNames("p3_name", "p3_middle", "p3_last", null)
        val person3 = com.koldyr.genealogy.model.Person(3, name3, events3, "place3", "occupation3", "note3", com.koldyr.genealogy.model.Gender.MALE, 1)

        val familyEvents = mutableSetOf(
            com.koldyr.genealogy.model.FamilyEvent(com.koldyr.genealogy.model.EventType.Engagement, null, LocalDate.of(1990, 10, 10), "place21", null),
            com.koldyr.genealogy.model.FamilyEvent(com.koldyr.genealogy.model.EventType.Marriage, null, LocalDate.of(1991, 10, 10), "place21", null)
        )

        val family = com.koldyr.genealogy.model.Family(1)
        family.wife = person1
        family.husband = person2
        family.children.add(person3)
        family.events.addAll(familyEvents)

        val persons = listOf(person1, person2, person3)
        val families = setOf(family)

        return com.koldyr.genealogy.model.Lineage(persons, families)
    }

    private fun assertPerson(expected: com.koldyr.genealogy.model.Person?, actual: com.koldyr.genealogy.model.Person?) {
        Assertions.assertEquals(expected!!.name, actual!!.name)
        Assertions.assertEquals(expected.gender, actual.gender)
        Assertions.assertEquals(expected.place, actual.place)
        Assertions.assertEquals(expected.occupation, actual.occupation)
        Assertions.assertEquals(expected.note, actual.note)
        Assertions.assertEquals(expected.parentFamilyId, actual.parentFamilyId)
        Assertions.assertEquals(expected.events, actual.events)
    }
}