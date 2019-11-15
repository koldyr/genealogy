package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Description of class GEDExporter
 * @created: 2019.10.31
 */
class GEDExporter : Exporter {

    override fun export(lineage: Lineage, file: Path) {
        export(lineage, Files.newOutputStream(file))
    }

    override fun export(lineage: Lineage, output: OutputStream) {
        output.bufferedWriter(Charsets.UTF_8).use { writer ->
            lineage.persons.forEach {
                writer.write(person(it))
            }

            lineage.families.forEach {
                writer.write(family(it))
            }
        }
    }

    private fun person(person: Person): String {
        val builder = StringBuilder()

        id(person.id, true, builder)
        name(person.name, builder)
        gender(person.gender, builder)
        place(person.place, builder)
        occupation(person.occupation, builder)
        note(person.note, builder)
        events(person.events, builder)

        return builder.toString()
    }

    private fun family(family: Family): String {
        val builder = StringBuilder()

        id(family.id, false, builder)
        events(family.events, builder)
        children(family.children, builder)
        note(family.note, builder)

        return builder.toString()
    }

    private fun id(id: Int, person: Boolean, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun name(person: PersonNames?, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun gender(gender: Gender, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun place(place: String?, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun occupation(occupation: String?, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun note(note: String?, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun events(events: MutableSet<LifeEvent>, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun children(children: Set<Person>, builder: StringBuilder) {
        TODO("not implemented")
    }
}
