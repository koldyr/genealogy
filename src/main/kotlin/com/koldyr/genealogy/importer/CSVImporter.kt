package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import org.apache.commons.lang3.StringUtils.*
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class CSVImporter : Importer {

    private val pattern = Pattern.compile("\\\\n")

    override fun import(file: File): Collection<Person> {
        val stream = Files.newInputStream(file.toPath())
        val charset = Charset.forName("windows-1251")
        return stream.bufferedReader(charset).use { reader ->
            val persons: MutableCollection<Person> = mutableListOf()

            var line: String? = reader.readLine()
            while (line != null) {
                val person: Person = readPerson(line)
                persons.add(person)

                line = reader.readLine()
            }

            return persons
        }
    }

    private fun readPerson(line: String): Person {
        val values = splitValues(line)

        val person = Person()
        for ((index, value) in values.withIndex()) {
            when (index) {
                0 -> person.id = Integer.parseInt(value)
                1 -> person.name = parseNames(value)
                2 -> person.birth = parseLifeEvent(value)
                3 -> person.death = parseLifeEvent(value)
                4 -> person.sex = Sex.valueOf(value)
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
                1 -> names.middle = name
                2 -> names.last = name
                3 -> names.maiden = name
            }
        }
        return names
    }

    private fun parseLifeEvent(value: String): LifeEvent? {
        if (isEmpty(value)) {
            return null
        }

        val event = LifeEvent()
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
                matcher.replaceAll("\n")
            }
        }
    }
}
