package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.apache.commons.lang3.StringUtils.*
import org.apache.commons.text.StringEscapeUtils.*
import java.io.BufferedWriter
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * Description of class CSVExporter
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
                writePerson(writer, person)
            }
            lineage.families.forEach { family ->
                writeFamily(writer, family)
            }
        }
    }

    private fun writePerson(writer: BufferedWriter, it: Person) {
        val line = StringJoiner(",", EMPTY, "\n")

        line.add('P' + it.id.toString())
        line.add(personNamesToCSV(it.name))
        line.add(eventsToCSV(it.events))
        line.add(it.gender.name)
        line.add(escapeCsv(it.place ?: EMPTY))
        line.add(escapeCsv(it.occupation ?: EMPTY))
        line.add(prepareNote(it.note))
        line.add(escapeCsv(it.familyId?.toString() ?: EMPTY))

        writer.write(line.toString())
    }

    private fun writeFamily(writer: BufferedWriter, it: Family) {
        val line = StringJoiner(",", EMPTY, "\n")

        line.add('F' + it.id.toString())
        line.add(if (it.husband == null) EMPTY else it.husband!!.id.toString())
        line.add(if (it.wife == null) EMPTY else it.wife!!.id.toString())
        line.add(personsToCSV(it.children))
        line.add(eventsToCSV(it.events))
        line.add(prepareNote(it.note))

        writer.write(line.toString())
    }

    private fun personNamesToCSV(name: PersonNames?): String {
        if (name == null) {
            return EMPTY
        }

        val value = StringBuilder()
        value.append(name.name)
        value.append('|')
        value.append(name.middle ?: EMPTY)
        value.append('|')
        value.append(name.last ?: EMPTY)
        value.append('|')
        value.append(name.maiden ?: EMPTY)
        return value.toString()
    }

    private fun eventsToCSV(events: Set<LifeEvent>?): String {
        if (events == null || events.isEmpty()) {
            return EMPTY
        }

        return events.stream()
                .map { eventToCSV(it) }
                .collect(Collectors.joining("!"))
    }

    private fun eventToCSV(it: LifeEvent): String {
        val value = StringBuilder()
        value.append(it.type.name)
        value.append('|')
        value.append(escapeCsv(it.prefix?.name ?: EMPTY))
        value.append('|')
        value.append(it.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: EMPTY)
        value.append('|')
        value.append(escapeCsv(it.place ?: EMPTY))
        return value.toString()
    }

    private fun personsToCSV(children: Set<Person>?): String {
        if (children == null || children.isEmpty()) {
            return EMPTY
        }

        return children.stream()
                .map { it.id.toString() }
                .collect(Collectors.joining("|"))
    }

    private fun prepareNote(note: String?): String? {
        if (isEmpty(note)) {
            return EMPTY
        }
        val matcher = pattern.matcher(note!!)
        return escapeCsv(matcher.replaceAll("\\\\n"))
    }
}
