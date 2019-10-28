package com.koldyr.genealogy.parser

import com.koldyr.genealogy.model.*
import java.io.File
import java.nio.charset.Charset
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import kotlin.math.max

/**
 * Description of class FamilyTreeDataParser
 *
 * @created: 2019-10-26
 */
class FamilyTreeDataParser {
    private val datePattern = DateTimeFormatter.ofPattern("yyyy MMM d")
    private val personIdPattern = Pattern.compile("@(\\d+)@")
    private val familyIdPattern = Pattern.compile("@\\w(\\d+)@")

    fun parse(fileName: String): Collection<Person> {
        val charset = Charset.forName("windows-1251")
        val bufferedReader = File(fileName).bufferedReader(charset)

        val families: MutableSet<Family> = mutableSetOf()
        val persons: MutableMap<Int, Person> = mutableMapOf()

        bufferedReader.use { reader ->
            var person: Person? = null
            var family: Family? = null
            var event: LifeEvent? = null

            var line: String? = reader.readLine()
            while (line != null) {
                if (line.endsWith(PERSON)) {
                    val personId: Int = getPersonId(line)
                    person = Person(personId)
                    persons.put(personId, person)
                } else if (line.contains(NAME)) {
                    if (person != null) {
                        person.name = parseFullName(line)
                    }
                } else if (line.contains(SEX)) {
                    if (person != null) {
                        person.sex = parseSex(line)
                    }
                } else if (line.contains(OCCUPATION)) {
                    if (person != null) {
                        person.occupation = parseGeneric(line, OCCUPATION)
                    }
                } else if (line.contains(RESIDENCE)) {
                    if (person != null) {
                        person.place = parseGeneric(line, RESIDENCE)
                    }
                } else if (line.contains(NOTE)) {
                    if (person != null) {
                        person.note = parseGeneric(line, NOTE)
                    } else if (family != null) {
                        family.note = parseGeneric(line, NOTE)
                    }
                } else if (line.contains(CONTINUE)) {
                    if (person != null) {
                        person.note = person.note + '\n' + parseGeneric(line, CONTINUE)
                    }
                } else if (line.contains(FAMC)) {
                    if (person != null) {
                        val familyId = parseFamilyId(line)
                        handleFamily(familyId, person, families)
                    }
                } else if (line.endsWith(FAMILY)) {
                    person = null
                    val familyId = parseFamilyId(line)
                    family = families.stream().filter { it.id == familyId }.findFirst().orElseGet { Family(familyId) }
                } else if (line.contains(HUSBAND)) {
                    if (family != null) {
                        val personId = getPersonId(line)
                        family.husband = persons.get(personId)
                    }
                } else if (line.contains(WIFE)) {
                    if (family != null) {
                        val personId = getPersonId(line)
                        family.wife = persons.get(personId)
                    }
                } else if (line.contains(CHILD)) {
                    if (family != null) {
                        val personId = getPersonId(line)
                        val child = persons.get(personId)
                        if (child != null) {
                            family.children.add(child)
                        }
                    }
                } else if (line.endsWith(BIRTH)) {
                    if (person != null) {
                        event = LifeEvent()
                        person.birth = event
                    }
                } else if (line.endsWith(DEATH)) {
                    if (person != null) {
                        event = LifeEvent()
                        person.death = event
                    }
                } else if (line.endsWith(MARRIAGE)) {
                    if (family != null) {
                        event = LifeEvent()
                        family.marriage = event
                    }
                } else if (line.contains(DATE)) {
                    if (event != null) {
                        event.date = parseDate(line)
                    }
                } else if (line.contains(PLACE)) {
                    if (event != null) {
                        event.place = parseGeneric(line, PLACE)
                    }
                }
                line = reader.readLine()
            }
        }

        return persons.values
    }

    private fun handleFamily(familyId: Int, person: Person, families: MutableSet<Family>) {
        val oFamily = families.stream().filter { it.id == familyId }.findFirst()

        if (!oFamily.isPresent) {
            families.add(Family(familyId))
        }

        person.familyId = familyId
    }

    private fun parseGeneric(line: String, name: String): String {
        return line.substring(line.indexOf(name) + name.length).trim()
    }

    private fun parseSex(line: String?): Sex {
        if (line == null) {
            return Sex.FEMALE
        }

        val sex = parseGeneric(line, SEX)
        if (sex.equals("M", true)) {
            return Sex.MALE
        }
        return Sex.FEMALE
    }

    private fun parseFullName(line: String): PersonNames {
        val value: String = parseGeneric(line, NAME)

        var fullName: String = value
        var maidenName: String? = null
        var lastName: String? = null

        if (value.contains('/')) {
            val start = value.indexOf('/')
            val end = value.indexOf('/', start + 1);

            fullName = value.substring(0, max(0, start - 1))
            val lastValue = value.substring(start + 1, end)
            lastName = lastValue

            if (lastValue.contains('(') && lastValue.contains(')')) {
                val mStart = lastValue.indexOf('(')
                val mEnd = lastValue.indexOf(')')
                lastName = lastValue.substring(0, mStart - 1)
                maidenName = lastValue.substring(mStart + 1, mEnd)
            }
        }
        val items = fullName.split(" ")
        val name = items[0];
        val middle = if (items.size == 2) items[1] else null
        return PersonNames(name, middle, lastName, maidenName)
    }

    private fun getPersonId(value: String): Int {
        val matcher = personIdPattern.matcher(value)
        if (matcher.find()) {
            val id = matcher.group(1)
            return Integer.parseInt(id)
        }
        return -1;
    }

    private fun parseFamilyId(value: String): Int {
        val matcher = familyIdPattern.matcher(value)
        if (matcher.find()) {
            val id = matcher.group(1)
            return Integer.parseInt(id)
        }
        return -1;
    }

    private fun parseDate(value: String): LocalDate {
        var dateValue = parseGeneric(value, DATE);

        if (dateValue.contains(ABOUT)) {
            dateValue = parseGeneric(dateValue, ABOUT);
        }
        if (dateValue.contains(AFTER)) {
            dateValue = parseGeneric(dateValue, AFTER);
        }
        if (dateValue.contains(BEFORE)) {
            dateValue = parseGeneric(dateValue, BEFORE);
        }

        val year: String
        val month: String
        val day: String

        val items = dateValue.split(" ")
        when (items.size) {
            1 -> {
                day = "1"
                month = "Jan"
                year = items[0]
            }
            2 -> {
                day = "1"
                month = parseMonth(items[0])
                year = items[1]
            }
            3 -> {
                day = items[0]
                month = parseMonth(items[1])
                year = items[2]
            }
            else -> {
                day = "1"
                month = "Jan"
                year = "1"
            }
        }

        return LocalDate.parse("$year $month $day", datePattern)
    }

    private fun parseMonth(month: String): String {
        val chars = month.toLowerCase().toCharArray()
        chars[0] = Character.toUpperCase(chars[0])
        return String(chars)
    }
}
