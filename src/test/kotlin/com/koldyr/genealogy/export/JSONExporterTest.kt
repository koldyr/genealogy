package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate

/**
 * Description of class JSONExporterTest
 *
 * @created: 2019-11-12
 */
class JSONExporterTest {

    @Test
    fun export() {
        val jsonExporter = JSONExporter()
        val events1 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1960, 10, 10), "place11"),
                LifeEvent(EventType.Death, null, LocalDate.of(2019, 10, 10), "place12")
        )
        val person1 = Person(1, PersonNames("p1_name", "p1_middle", "p1_last", "p1_maiden"), events1, "place1", "occupation1", "note1", Sex.FEMALE, 1)

        val events2 = mutableSetOf(
                LifeEvent(EventType.Birth, null, LocalDate.of(1970, 10, 10), "place21"),
                LifeEvent(EventType.Death, null, LocalDate.of(2029, 10, 10), "place22")
        )
        val person2 = Person(2, PersonNames("p2_name", "p2_middle", "p2_last", null), events2, "place")

        val persons = listOf(person1, person2)
        jsonExporter.export(File("./lineage.json"), Lineage(persons, setOf()))
    }
}
