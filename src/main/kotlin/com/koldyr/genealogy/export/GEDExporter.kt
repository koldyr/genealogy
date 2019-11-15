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
        if (person) {
            builder.append("0 @").append(id).append("@ INDI")
        } else {
            builder.append("0 @F").append(id).append("@ FAM")
        }
    }

    private fun name(names: PersonNames?, builder: StringBuilder) {
        if (names != null) {
            val nameBuilder = StringBuilder()

            if (names.name != null) {
                builder.append(names.name)
            }
            if (names.middle != null) {
                if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')
                builder.append(names.middle)
            }
            if (names.last != null || names.maiden != null) {
                if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')

                builder.append('/')

                if (names.last != null) {
                    builder.append(names.last)
                }

                if (names.maiden != null) {
                    if (names.last != null) nameBuilder.append(' ')
                    builder.append('(').append(names.maiden).append(')')
                }

                builder.append('/')
            }

            nameBuilder.insert(0, "1 NAME ")

            builder.append(nameBuilder)
        }
    }

    private fun gender(gender: Gender, builder: StringBuilder) {
        builder.append("1 SEX ").append(if (gender == Gender.FEMALE) 'F' else 'M')
    }

    private fun place(place: String?, builder: StringBuilder) {
        if (place != null) {
            builder.append("1 RESI ").append(place)
        }
    }

    private fun occupation(occupation: String?, builder: StringBuilder) {
        if (occupation != null) {
            builder.append("1 OCCU ").append(occupation)
        }
    }

    private fun note(note: String?, builder: StringBuilder) {
        if (note != null) {
            builder.append("1 NOTE ").append(note)
        }
    }

    private fun events(events: MutableSet<LifeEvent>, builder: StringBuilder) {
        TODO("not implemented")
    }

    private fun children(children: Set<Person>, builder: StringBuilder) {
        TODO("not implemented")
    }
}
