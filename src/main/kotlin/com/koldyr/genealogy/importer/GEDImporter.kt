package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.apache.commons.lang3.StringUtils
import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import kotlin.math.max

const val PERSON = "INDI"
const val CHAR_ENCODING = "CHAR"
const val NAME = "NAME"
const val SEX = "SEX"
const val BIRTH = "BIRT"
const val DEATH = "DEAT"
const val DATE = "DATE"
const val PLACE = "PLAC"
const val RESIDENCE = "RESI"
const val OCCUPATION = "OCCU"
const val NOTE = "NOTE"
const val CONTINUE = "CONT"
const val FAMC = "FAMC"
const val FAMILY = "FAM"
const val MARRIAGE = "MARR"
const val DIVORCE = "DIV"
const val HUSBAND = "HUSB"
const val WIFE = "WIFE"
const val CHILD = "CHIL"
const val ABOUT = "ABT"
const val AFTER = "AFT"
const val BEFORE = "BEF"

/**
 * Description of class GEDImporter
 * @created: 2019.10.31
 */
class GEDImporter : Importer {
    private val datePattern = DateTimeFormatter.ofPattern("yyyy MMM d")
    private val personIdPattern = Pattern.compile("@(\\d+)@")
    private val familyIdPattern = Pattern.compile("@\\w(\\d+)@")

    override fun import(file: Path): Lineage {
        return import(Files.newInputStream(file))
    }

    override fun import(input: InputStream): Lineage {
        val families: MutableSet<Family> = mutableSetOf()
        val persons: MutableMap<Int, Person> = mutableMapOf()

        val rewindInput = if (input.markSupported()) input else BufferedInputStream(input)
        rewindInput.mark(rewindInput.available())

        val charset = getEncoding(rewindInput)
        rewindInput.reset()

        rewindInput.bufferedReader(charset).use { reader ->
            var person: Person? = null
            var family: Family? = null
            var event: LifeEvent? = null

            var line: String? = reader.readLine()
            while (line != null) {
                if (line.endsWith(PERSON)) {
                    val personId: Int = getPersonId(line)
                    person = Person(personId)
                    persons[personId] = person
                    event = null
                } else if (line.contains(NAME)) {
                    if (person != null) {
                        person.name = parseFullName(line)
                    }
                } else if (line.contains(SEX)) {
                    if (person != null) {
                        person.gender = parseSex(line)
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
                    family = findFamily(families, familyId)
                } else if (line.contains(HUSBAND)) {
                    if (family != null) {
                        val personId = getPersonId(line)
                        family.husband = persons[personId]
                    }
                } else if (line.contains(WIFE)) {
                    if (family != null) {
                        val personId = getPersonId(line)
                        family.wife = persons[personId]
                    }
                } else if (line.contains(CHILD)) {
                    if (family != null) {
                        val personId = getPersonId(line)
                        val child = persons[personId]
                        if (child != null) {
                            family.children.add(child)
                        }
                    }
                } else if (line.endsWith(BIRTH)) {
                    if (person != null) {
                        event = LifeEvent(EventType.Birth)
                        person.events.add(event)
                    }
                } else if (line.endsWith(DEATH)) {
                    if (person != null) {
                        event = LifeEvent(EventType.Death)
                        person.events.add(event)
                    }
                } else if (line.endsWith(MARRIAGE)) {
                    if (family != null) {
                        event = LifeEvent(EventType.Marriage)
                        family.events.add(event)
                    }
                } else if (line.endsWith(DIVORCE)) {
                    if (family != null) {
                        event = LifeEvent(EventType.Divorce)
                        family.events.add(event)
                    }
                } else if (line.contains(DATE)) {
                    if (event != null) {
                        val parsed = parseDate(line)
                        event.date = parsed.first
                        event.prefix = parsed.second
                    }
                } else if (line.contains(PLACE)) {
                    if (event != null) {
                        event.place = parseGeneric(line, PLACE)
                    } else if (person != null) {
                        person.place = parseGeneric(line, PLACE)
                    }
                }
                line = reader.readLine()
            }
        }

        return Lineage(persons.values, families)
    }

    private fun findFamily(families: MutableSet<Family>, familyId: Int) =
            families.stream().filter { it.id == familyId }.findFirst().orElseGet { Family(familyId) }

    private fun getEncoding(input: InputStream): Charset {
        val charset = Charsets.UTF_8

        val reader = input.bufferedReader(charset)
        var line: String? = reader.readLine()
        while (line != null) {
            if (line.contains(CHAR_ENCODING)) {
                return Charset.forName(parseGeneric(line, CHAR_ENCODING))
            }
            line = reader.readLine()
        }

        return charset
    }

    private fun handleFamily(familyId: Int, person: Person, families: MutableSet<Family>) {
        val noFamily = families.stream().noneMatch { it.id == familyId }
        if (noFamily) {
            families.add(Family(familyId))
        }

        person.familyId = familyId
    }

    private fun parseGeneric(line: String, name: String): String {
        return line.substring(line.indexOf(name) + name.length).trim()
    }

    private fun parseSex(line: String?): Gender {
        if (line == null) {
            return Gender.FEMALE
        }

        val sex = parseGeneric(line, SEX)
        if (sex.equals("M", true)) {
            return Gender.MALE
        }
        return Gender.FEMALE
    }

    private fun parseFullName(line: String): PersonNames {
        val value: String = parseGeneric(line, NAME)

        var fullName: String = value
        var maidenName: String? = null
        var lastName: String? = null

        if (value.contains('/')) {
            val start = value.indexOf('/')
            val end = value.indexOf('/', start + 1)

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
        val name = items[0]
        val middle = if (items.size == 2) items[1] else null
        return PersonNames(name, middle, lastName, maidenName)
    }

    private fun getPersonId(value: String): Int {
        val matcher = personIdPattern.matcher(value)
        if (matcher.find()) {
            val id = matcher.group(1)
            return Integer.parseInt(id)
        }
        return -1
    }

    private fun parseFamilyId(value: String): Int {
        val matcher = familyIdPattern.matcher(value)
        if (matcher.find()) {
            val id = matcher.group(1)
            return Integer.parseInt(id)
        }
        return -1
    }

    private fun parseDate(value: String): Pair<LocalDate, EventPrefix?> {
        var dateValue = parseGeneric(value, DATE)

        val prefix: EventPrefix? = when {
            dateValue.contains(BEFORE) -> {
                dateValue = parseGeneric(dateValue, BEFORE)
                EventPrefix.Before
            }
            dateValue.contains(ABOUT) -> {
                dateValue = parseGeneric(dateValue, ABOUT)
                EventPrefix.About
            }
            dateValue.contains(AFTER) -> {
                dateValue = parseGeneric(dateValue, AFTER)
                EventPrefix.After
            }
            else -> null
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

        val date = LocalDate.parse("$year $month $day", datePattern)
        return Pair(date, prefix)
    }

    private fun parseMonth(month: String): String {
        return StringUtils.capitalize(month.toLowerCase())
    }
}
