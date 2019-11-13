package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.apache.commons.lang3.StringUtils.*
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class CSVImporter : Importer {
    private val pattern = Pattern.compile("\\\\n")

    override fun import(file: Path): Lineage {
        return import(Files.newInputStream(file))
    }

    override fun import(input: InputStream): Lineage {
        val persons = mutableListOf<Person>()
        val families = mutableSetOf<Family>()
        input.bufferedReader(Charsets.UTF_8).use { reader ->
            reader.lines().forEach { line ->
                if (line.startsWith('P')) {
                    persons.add(readPerson(line))
                } else {
                    families.add(readFamily(line))
                }
            }
        }

        val lineage = Lineage(persons, families)

        families.forEach {
            if (it.wife != null) {
                val person = lineage.findPerson(it.wife?.id)
                if (person != null) {
                    it.wife = person
                }
            }

            if (it.husband != null) {
                val person = lineage.findPerson(it.husband?.id)
                if (person != null) {
                    it.husband = person
                }
            }

            for (child in it.children.toSet()) {
                val person = lineage.findPerson(child.id)
                if (person != null) {
                    it.children.remove(child)
                    it.children.add(person)
                }
            }
        }

        return lineage
    }

    private fun readPerson(line: String): Person {
        val values = splitValues(line)

        val person = Person(Integer.parseInt(values[0].removePrefix("P")))
        for ((index, value) in values.withIndex()) {
            when (index) {
                1 -> person.name = parseNames(value)
                2 -> person.events.addAll(parseLifeEvents(value))
                4 -> person.gender = Gender.valueOf(value)
                5 -> person.place = value
                6 -> person.occupation = value
                7 -> person.note = parseNote(value)
                8 -> person.familyId = if (isEmpty(value)) null else Integer.parseInt(value)
            }
        }
        return person
    }

    private fun readFamily(line: String): Family {
        val values = line.split(',')

        val family = Family(Integer.parseInt(values[0].removePrefix("F")))
        for ((index, value) in values.withIndex()) {
            when (index) {
                1 -> family.husband = if (isEmpty(value)) null else Person(Integer.parseInt(value))
                2 -> family.wife = if (isEmpty(value)) null else Person(Integer.parseInt(value))
                3 -> family.children.addAll(parsePersons(value))
                4 -> family.events.addAll(parseLifeEvents(value))
                5 -> family.note = parseNote(value)
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
                0 -> names.name = name
                1 -> names.middle = defaultIfEmpty(name, null)
                2 -> names.last = defaultIfEmpty(name, null)
                3 -> names.maiden = defaultIfEmpty(name, null)
            }
        }
        return names
    }

    private fun parseLifeEvents(value: String): Collection<LifeEvent> {
        if (isEmpty(value)) {
            return setOf()
        }

        val values = value.split('!')
        return values.map { parseLifeEvent(it) }
    }

    private fun parseLifeEvent(value: String): LifeEvent {
        val values = value.split('|')

        val event = LifeEvent(EventType.valueOf(values[0]))
        for ((index, v) in values.withIndex()) {
            when (index) {
                1 -> event.prefix = if (isEmpty(v)) null else EventPrefix.valueOf(v)
                2 -> event.date = parseDate(v)
                3 -> event.place = v
            }
        }

        return event
    }

    private fun parsePersons(value: String): Collection<Person> {
        val values = value.split('|')
        return values.map { Person(Integer.parseInt(it)) }
    }

    private fun parseDate(date: String): LocalDate? {
        return if (isEmpty(date)) {
            null
        } else LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun parseNote(value: String): String? {
        return when {
            isEmpty(value) -> null
            else -> {
                val matcher = pattern.matcher(value)
                matcher.replaceAll('\n'.toString())
            }
        }
    }
}
