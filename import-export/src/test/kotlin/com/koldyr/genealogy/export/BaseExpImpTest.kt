package com.koldyr.genealogy.export

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import org.apache.commons.lang3.RandomStringUtils.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.PersonNames

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

    private fun createLineage(): Lineage {
        val personIds = LongArray(100) { i -> 1000 + i.toLong() }.iterator()
        val familyIds = LongArray(100) { i -> 2000 + i.toLong() }.iterator()

        val husband = createPerson(Gender.MALE, personIds.next())
        val wife = createPerson(Gender.FEMALE, personIds.next())
        val children1 = listOf(
            createPerson(Gender.FEMALE, personIds.next()),
            createPerson(Gender.MALE, personIds.next())
        )
        val family1 = newFamily(familyIds, wife, husband, children1)

        val family2 = newFamily(familyIds,
            children1.get(0),
            createPerson(Gender.MALE, personIds.next()),
            listOf(
                createPerson(Gender.FEMALE, personIds.next()),
                createPerson(Gender.MALE, personIds.next())
            )
        )

        val family3 = newFamily(familyIds,
            createPerson(Gender.FEMALE, personIds.next()),
            children1.get(1),
            listOf(
                createPerson(Gender.FEMALE, personIds.next()),
                createPerson(Gender.MALE, personIds.next())
            )
        )

        val families = setOf(family1, family2, family3)
        val persons = families
            .map {
                val family = mutableSetOf(it.wife, it.husband)
                family.addAll(it.children)
                family
            }
            .flatten()
            .filterNotNull()
            .toSet()

        val lineage = Lineage(persons, families, true)
        lineage.name = "Test Lineage"
        lineage.note = "Test Lineage"
        return lineage
    }

    private fun createRandomWord(): String {
        return randomAlphabetic(10)
    }

    private fun createPerson(gender: Gender, id: Long): Person {
        val person = Person(id)
        person.name = PersonNames(createRandomWord(), createRandomWord(), createRandomWord(), null)
        person.gender = gender
        person.place = createRandomWord()
        person.occupation = createRandomWord()
        person.note = createRandomWord()
        person.events.add(createLifeEvent(EventType.Birth, 1990))
        return person
    }

    private fun createLifeEvent(type: EventType, startYear: Int): PersonEvent {
        val year = (startYear..startYear + 20).random()
        val month = (1..12).random()
        val day = (1..28).random()
        val place = (1..100_000).random()
        val note = (1..100_000).random()
        return PersonEvent(type, null, LocalDate.of(year, month, day), "place $place", "note $note")
    }

    private fun newFamily(familyIds: LongIterator, wife: Person, husband: Person, children: List<Person>): Family {
        val family = Family(familyIds.next())
        family.wife = wife
        family.husband = husband
        family.children.addAll(children)
        family.events.add(createLifeEvent(EventType.Marriage, 2020).toFamilyEvent())

        wife.familyId = family.id
        husband.familyId = family.id
        children.forEach { it.parentFamilyId = family.id }

        return family
    }

    private fun assertPerson(expected: Person?, actual: Person?) {
        Assertions.assertEquals(expected!!.name, actual!!.name)
        Assertions.assertEquals(expected.gender, actual.gender)
        Assertions.assertEquals(expected.place, actual.place)
        Assertions.assertEquals(expected.occupation, actual.occupation)
        Assertions.assertEquals(expected.note, actual.note)
        Assertions.assertEquals(expected.parentFamilyId, actual.parentFamilyId)
        Assertions.assertEquals(expected.events, actual.events)
    }
}