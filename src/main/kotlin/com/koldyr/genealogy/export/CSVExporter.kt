package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Clan
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.apache.commons.lang3.StringUtils.*
import org.apache.commons.text.StringEscapeUtils.*
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

/**
 * Description of class CSVExporter
 * @created: 2019.10.31
 */
class CSVExporter : Exporter {

    private val pattern = Pattern.compile("\\n")

    override fun export(file: File, clan: Clan) {
        val stream = Files.newOutputStream(file.toPath())
        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
            clan.persons.forEach {
                person -> writePerson(writer, person)
            }
        }
    }

    private fun writePerson(writer: BufferedWriter, it: Person) {
        val line = StringJoiner(",", EMPTY, "\n")

        line.add(it.id.toString())
        line.add(personNamesToCSV(it.name))
        line.add(liveEventToCSV(it.birth))
        line.add(liveEventToCSV(it.death))
        line.add(it.sex.name)
        line.add(escapeCsv(it.place ?: EMPTY))
        line.add(escapeCsv(it.occupation ?: EMPTY))
        line.add(prepareNote(it.note))
        line.add(escapeCsv(it.familyId?.toString() ?: EMPTY))

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

    private fun liveEventToCSV(event: LifeEvent?): String {
        if (event == null || event.date == null && event.place == null) {
            return EMPTY
        }

        val value = StringBuilder()
        value.append(event.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: EMPTY)
        value.append('|')
        value.append(escapeCsv(event.place ?: EMPTY))
        return value.toString()
    }

    private fun prepareNote(note: String?): String? {
        if (isEmpty(note)) {
            return EMPTY
        }
        val matcher = pattern.matcher(note!!)
        return escapeCsv(matcher.replaceAll("\\\\n"))
    }
}
