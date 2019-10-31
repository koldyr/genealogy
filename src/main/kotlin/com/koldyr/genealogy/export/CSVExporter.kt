package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import java.io.File
import java.nio.file.Files
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Description of class CSVExporter
 * @created: 2019.10.31
 */
class CSVExporter : Exporter {
    override fun export(file: File, persons: Collection<Person>) {
        val stream = Files.newOutputStream(file.toPath())
        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
            persons.forEach {
                val line = StringJoiner(",", "", "\n")

                line.add(it.id.toString())
                line.add(personNamesToCSV(it.name))
                line.add(liveEventToCSV(it.birth))
                line.add(liveEventToCSV(it.death))
                line.add(it.sex.name)
                line.add(it.place ?: "")
                line.add(it.occupation ?: "")
                line.add(it.note ?: "")
                line.add(it.familyId?.toString() ?: "")

                writer.write(line.toString())
            }
        }
    }

    private fun personNamesToCSV(name: PersonNames?): String {
        if (name == null) {
            return ""
        }

        val value = StringBuilder()
        value.append(name.name)
        value.append('|')
        value.append(name.middle ?: "")
        value.append('|')
        value.append(name.last ?: "")
        value.append('|')
        value.append(name.maiden ?: "")
        return value.toString()
    }

    private fun liveEventToCSV(event: LifeEvent?): String {
        if (event == null || event.date == null && event.place == null) {
            return ""
        }

        val value = StringBuilder()
        value.append(event.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "")
        value.append('|')
        value.append(event.place ?: "")
        return value.toString()
    }
}
