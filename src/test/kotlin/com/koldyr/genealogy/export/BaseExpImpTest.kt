package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate

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
            assertEquals(it, family)
        }

        lineage.persons.forEach {
            val person = result.findPerson(it.id)
            assertNotNull(person)
            assertEquals(it.name, person!!.name)
            assertEquals(it.gender, person.gender)
            assertEquals(it.place, person.place)
            assertEquals(it.occupation, person.occupation)
            assertEquals(it.note, person.note)
            assertEquals(it.parentFamily, person.parentFamily)
            assertEquals(it.events, person.events)
        }
    }

    protected abstract fun getImporter(): Importer

    protected abstract fun getExporter(): Exporter

    private fun createLineage(): Lineage {
        val events1 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1960, 10, 10), "place11"),
                LifeEvent(EventType.Education, EventPrefix.After, LocalDate.of(1970, 1, 1), "place12")
        )
        val name1 = PersonNames("p1_name", "p1_middle", "p1_last", "p1_maiden")
        val person1 = Person(1, name1, events1, "place1", "occupation1", "person1\nnote", Gender.FEMALE, null, 1)

        val events2 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1970, 10, 10), "place21", "person2 birth\nnotes"),
                LifeEvent(EventType.Christening, EventPrefix.About, LocalDate.of(1980, 1, 1), "place22")
        )
        val name2 = PersonNames("p2_name", "p2_middle", "p2_last", null)
        val person2 = Person(2, name2, events2, "place2", "occupation2", "note2\nnote2n", Gender.MALE, null, 1)

        val events3 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1995, 10, 10), "place21")
        )
        val name3 = PersonNames("p3_name", "p3_middle", "p3_last", null)
        val person3 = Person(3, name3, events3, "place3", "occupation3", "note3", Gender.MALE, 1)

        val familyEvents = mutableSetOf(
                LifeEvent(EventType.Engagement, null, LocalDate.of(1990, 10, 10), "place21"),
                LifeEvent(EventType.Marriage, null, LocalDate.of(1991, 10, 10), "place21")
        )

        val family = Family(1)
        family.wife = person1
        family.husband = person2
        family.children.add(person3)
        family.events.addAll(familyEvents)

        val persons = listOf(person1, person2, person3)
        val families = setOf(family)

        return Lineage(persons, families)
    }
}
