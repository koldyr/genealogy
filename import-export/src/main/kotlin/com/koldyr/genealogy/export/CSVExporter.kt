package com.koldyr.genealogy.export

import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter.*
import java.util.StringJoiner
import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils.*
import org.apache.commons.text.StringEscapeUtils.*
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames

/**
 * Description of class CSVExporter
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
class CSVExporter : Exporter {
    private val pattern = Pattern.compile("\\n")

    override fun export(lineage: Lineage, file: Path) {
        export(lineage, Files.newOutputStream(file))
    }

    override fun export(lineage: Lineage, output: OutputStream) {
        output.bufferedWriter(Charsets.UTF_8).use { writer ->
            lineage.persons.forEach { person ->
                writer.write(person(person))
            }
            lineage.families.forEach { family ->
                writer.write(family(family))
            }
        }
    }

    private fun person(it: Person): String {
        val line = StringJoiner(",", EMPTY, "\n")

        line.add('P' + it.id.toString())
        line.add(personNames(it.name))
        line.add(events(it.events))
        line.add(it.gender.name)
        line.add(escapeCsv(it.place ?: EMPTY))
        line.add(escapeCsv(it.occupation ?: EMPTY))
        line.add(note(it.note))
        line.add(it.parentFamilyId?.toString() ?: EMPTY)
        line.add(it.familyId?.toString() ?: EMPTY)
        return line.toString()
    }

    private fun family(it: Family): String {
        val line = StringJoiner(",", EMPTY, "\n")

        line.add('F' + it.id.toString())
        line.add(it.husband?.id?.toString() ?: EMPTY)
        line.add(it.wife?.id?.toString() ?: EMPTY)
        line.add(persons(it.children))
        line.add(events(it.events))
        line.add(note(it.note))
        return line.toString()
    }

    private fun personNames(name: PersonNames?): String {
        if (name == null) {
            return EMPTY
        }

        val value = StringBuilder()
        value.append(name.first)
        value.append('|')
        value.append(name.middle ?: EMPTY)
        value.append('|')
        value.append(name.last ?: EMPTY)
        value.append('|')
        value.append(name.maiden ?: EMPTY)
        return value.toString()
    }

    private fun events(events: Set<LifeEvent>): String {
        if (events.isEmpty()) {
            return EMPTY
        }

        return events.joinToString("!") { event(it) }
    }

    private fun event(it: LifeEvent): String {
        val value = StringBuilder()
        value.append(it.type.name)
        value.append('|')
        value.append(escapeCsv(it.prefix?.name ?: EMPTY))
        value.append('|')
        value.append(it.date?.format(ISO_LOCAL_DATE) ?: EMPTY)
        value.append('|')
        value.append(escapeCsv(it.place ?: EMPTY))
        value.append('|')
        value.append(note(it.note))
        return value.toString()
    }

    private fun persons(children: Set<Person>?): String {
        if (children.isNullOrEmpty()) {
            return EMPTY
        }

        return children.joinToString("|") { it.id.toString() }
    }

    private fun note(note: String?): String? {
        if (isEmpty(note)) {
            return EMPTY
        }
        val matcher = pattern.matcher(note!!)
        return escapeCsv(matcher.replaceAll("\\\\n"))
    }
}
