package com.koldyr.genealogy.importer

import java.io.InputStream
import java.nio.file.Files.*
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter.*
import java.util.regex.Pattern
import kotlin.text.Charsets.UTF_8
import org.apache.commons.lang3.StringUtils.*
import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.FamilyEvent
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.PersonNames

class CSVImporter : Importer {
    private val pattern = Pattern.compile("\\\\n")
    private val persons = mutableListOf<Person>()
    private val families = mutableSetOf<Family>()

    override fun import(file: Path): Lineage {
        return import(newInputStream(file))
    }

    override fun import(input: InputStream): Lineage {
        input.bufferedReader(UTF_8).use { reader ->
            reader.lines().forEach { line ->
                if (line.startsWith('P')) {
                    persons.add(readPerson(line))
                } else {
                    families.add(readFamily(line))
                }
            }
        }

        return Lineage(persons, families, true)
    }

    private fun readPerson(line: String): Person {
        val values = splitValues(line)

        val person = Person(java.lang.Long.parseLong(values[0].removePrefix("P")))

        for ((index, value) in values.withIndex()) {
            when (index) {
                1 -> person.name = parseNames(value)
                2 -> person.events.addAll(personEvents(value))
                3 -> person.gender = Gender.valueOf(value)
                4 -> person.place = value
                5 -> person.occupation = value
                6 -> person.note = note(value)
                7 -> person.parentFamilyId = if (isEmpty(value)) null else java.lang.Long.parseLong(value)
                8 -> person.familyId = if (isEmpty(value)) null else java.lang.Long.parseLong(value)
            }
        }
        return person
    }

    private fun readFamily(line: String): Family {
        val values = line.split(',')

        val family = Family(java.lang.Long.parseLong(values[0].removePrefix("F")))

        for ((index, value) in values.withIndex()) {
            when (index) {
                1 -> family.husband = if (isEmpty(value)) null else Person(java.lang.Long.parseLong(value))
                2 -> family.wife = if (isEmpty(value)) null else Person(java.lang.Long.parseLong(value))
                3 -> family.children.addAll(persons(value))
                4 -> family.events.addAll(familyEvents(value))
                5 -> family.note = note(value)
            }
        }
        return family
    }

    private fun splitValues(line: String): List<String> {
        val result = mutableListOf<String>()

        var completed = true
        val buffer = StringBuilder()
        val chars = line.toCharArray().iterator()
        while (chars.hasNext()) {
            val char = chars.next()

            if (char == ',' && completed) {
                result.add(buffer.toString())
                buffer.clear()
                continue
            }

            if (char == '"') {
                completed = !completed
                continue
            }

            buffer.append(char)
        }

        result.add(buffer.toString())

        return result
    }

    private fun parseNames(value: String): PersonNames {
        val names = PersonNames(value)
        val values = value.split('|')
        for ((index, name) in values.withIndex()) {
            when (index) {
                0 -> names.first = name
                1 -> names.middle = defaultIfEmpty(name, null)
                2 -> names.last = defaultIfEmpty(name, null)
                3 -> names.maiden = defaultIfEmpty(name, null)
            }
        }
        return names
    }

    private fun personEvents(value: String): Collection<PersonEvent> {
        if (isEmpty(value)) {
            return setOf()
        }

        return value.split('!').map {
            parseLifeEvent(it, false) as PersonEvent
        }
    }

    private fun familyEvents(value: String): Collection<FamilyEvent> {
        if (isEmpty(value)) {
            return setOf()
        }

        return value.split('!').map {
            parseLifeEvent(it, true) as FamilyEvent
        }
    }

    private fun parseLifeEvent(value: String, isFamily: Boolean): LifeEvent {
        val values = value.split('|')

        val eventType = EventType.valueOf(values[0])
        val event = if (isFamily) FamilyEvent(eventType) else PersonEvent(eventType)

        for ((index, v) in values.withIndex()) {
            when (index) {
                1 -> event.prefix = if (isEmpty(v)) null else EventPrefix.valueOf(v)
                2 -> event.date = parseDate(v)
                3 -> event.place = v
                4 -> event.note = note(v)
            }
        }

        return event
    }

    private fun persons(value: String): Collection<Person> {
        return value.split('|').map {
            Person(java.lang.Long.parseLong(it))
        }
    }

    private fun parseDate(date: String): LocalDate? {
        return if (isEmpty(date)) {
            null
        } else LocalDate.parse(date, ISO_LOCAL_DATE)
    }

    private fun note(value: String): String? {
        return when {
            isEmpty(value) -> null
            else -> {
                val matcher = pattern.matcher(value)
                matcher.replaceAll('\n'.toString())
            }
        }
    }
}
