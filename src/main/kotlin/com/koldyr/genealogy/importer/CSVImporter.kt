package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.apache.commons.lang3.StringUtils.*
import java.io.BufferedReader
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
        val persons = input.bufferedReader(Charsets.UTF_8).use { reader ->
            parse(reader)
        }
        return Lineage(persons, setOf())
    }

    private fun parse(reader: BufferedReader): Collection<Person> {
        val persons: MutableCollection<Person> = mutableListOf()

        var line: String? = reader.readLine()
        while (line != null) {
            val person: Person = readPerson(line)
            persons.add(person)

            line = reader.readLine()
        }

        return persons
    }

    private fun readPerson(line: String): Person {
        val values = splitValues(line)

        val person = Person(-1)
        for ((index, value) in values.withIndex()) {
            when (index) {
                0 -> person.id = Integer.parseInt(value)
                1 -> person.name = parseNames(value)
                2 -> person.events.add(parseLifeEvent(value, EventType.Birth))
                3 -> person.events.add(parseLifeEvent(value, EventType.Death))
                4 -> person.gender = Gender.valueOf(value)
                5 -> person.place = value
                6 -> person.occupation = value
                7 -> person.note = parseNote(value)
                8 -> person.familyId = if (isEmpty(value)) null else Integer.parseInt(value)
            }
        }
        return person
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

    private fun parseLifeEvent(value: String, type: EventType): LifeEvent {
        val event = LifeEvent(type)

        if (isEmpty(value)) {
            return event
        }

        val values = value.split('|')
        for ((index, v) in values.withIndex()) {
            when (index) {
                0 -> event.date = parseDate(v)
                1 -> event.place = v
            }
        }
        return event
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
