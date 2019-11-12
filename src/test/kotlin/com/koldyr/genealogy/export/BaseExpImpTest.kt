package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import org.junit.jupiter.api.Assertions
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
    fun export() {
        val output = ByteArrayOutputStream()

        val lineage = createLineage()
        getExporter().export(lineage, output)

        println(String(output.toByteArray(), Charsets.UTF_8))

        val input = ByteArrayInputStream(output.toByteArray())
        val result = getImporter().import(input)

        Assertions.assertEquals(lineage, result)
    }

    protected abstract fun getImporter(): Importer

    protected abstract fun getExporter(): Exporter

    private fun createLineage(): Lineage {
        val events1 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1960, 10, 10), "place11"),
                LifeEvent(EventType.Death, null, LocalDate.of(2019, 10, 10), "place12")
        )
        val person1 = Person(1, PersonNames("p1_name", "p1_middle", "p1_last", "p1_maiden"), events1, "place1", "occupation1", "note1", Sex.FEMALE, 1)

        val events2 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1970, 10, 10), "place21"),
                LifeEvent(EventType.Death, null, LocalDate.of(2029, 10, 10), "place22")
        )
        val person2 = Person(2, PersonNames("p2_name", "p2_middle", "p2_last", null), events2, "place2", "occupation2", "note2", Sex.MALE, 1)

        val events3 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1990, 10, 10), "place21")
        )
        val person3 = Person(3, PersonNames("p3_name", "p3_middle", "p3_last", null), events3, "place3", "occupation3", "note3", Sex.MALE, 1)

        val family = Family(1)
        family.wife = person1
        family.husband = person2
        family.children.add(person3)

        val persons = listOf(person1, person2, person3)
        val families = setOf(family)

        return Lineage(persons, families)
    }
}
