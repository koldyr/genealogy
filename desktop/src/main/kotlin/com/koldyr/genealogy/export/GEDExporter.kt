package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.CHILD
import com.koldyr.genealogy.importer.CONTINUE
import com.koldyr.genealogy.importer.DATE
import com.koldyr.genealogy.importer.FAMILY
import com.koldyr.genealogy.importer.HUSBAND
import com.koldyr.genealogy.importer.NAME
import com.koldyr.genealogy.importer.NOTE
import com.koldyr.genealogy.importer.OCCUPATION
import com.koldyr.genealogy.importer.OWN_FAMILY
import com.koldyr.genealogy.importer.PARENT_FAMILY
import com.koldyr.genealogy.importer.PERSON
import com.koldyr.genealogy.importer.PLACE
import com.koldyr.genealogy.importer.RESIDENCE
import com.koldyr.genealogy.importer.SEX
import com.koldyr.genealogy.importer.WIFE
import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter

/**
 * Description of class GEDExporter
 * @created: 2019.10.31
 */
class GEDExporter : Exporter {
    private val datePattern = DateTimeFormatter.ofPattern("d MMM yyyy")

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

        id(person.id!!, true, builder)
        name(person.name, builder)
        gender(person.gender, builder)
        place(person.place, builder)
        occupation(person.occupation, builder)
        note(person.note, builder)
        events(person.events, builder)
        families(person, builder)

        return builder.toString()
    }

    private fun families(person: Person, builder: StringBuilder) {
        if (person.familyId != null) {
            builder.append("1 ").append(OWN_FAMILY).append(" @F").append(person.familyId!!).append("@\n")
        }
        if (person.parentFamilyId != null) {
            builder.append("1 ").append(PARENT_FAMILY).append(" @F").append(person.parentFamilyId!!).append("@\n")
        }
    }

    private fun family(family: Family): String {
        val builder = StringBuilder()

        id(family.id!!, false, builder)
        person(family.husband, HUSBAND, builder)
        person(family.wife, WIFE, builder)
        children(family.children, builder)
        events(family.events, builder)
        note(family.note, builder)

        return builder.toString()
    }

    private fun id(id: Int, person: Boolean, builder: StringBuilder) {
        if (person) {
            builder.append("0 @").append(id).append("@ ").append(PERSON).append('\n')
        } else {
            builder.append("0 @F").append(id).append("@ ").append(FAMILY).append('\n')
        }
    }

    private fun name(names: PersonNames?, builder: StringBuilder) {
        if (names != null) {
            val nameBuilder = StringBuilder()

            if (names.first != null) {
                nameBuilder.append(names.first)
            }
            if (names.middle != null) {
                if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')
                nameBuilder.append(names.middle)
            }
            if (names.last != null || names.maiden != null) {
                if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')

                nameBuilder.append('/')

                if (names.last != null) {
                    nameBuilder.append(names.last)
                }

                if (names.maiden != null) {
                    if (names.last != null) nameBuilder.append(' ')
                    nameBuilder.append('(').append(names.maiden).append(')')
                }

                nameBuilder.append('/')
            }

            nameBuilder.insert(0, "1 $NAME ")

            builder.append(nameBuilder).append('\n')
        }
    }

    private fun gender(gender: Gender, builder: StringBuilder) {
        builder.append("1 ").append(SEX).append(" ").append(if (gender == Gender.FEMALE) 'F' else 'M').append('\n')
    }

    private fun place(place: String?, builder: StringBuilder) {
        if (place != null) {
            builder.append("1 ").append(RESIDENCE).append(" ").append(place).append('\n')
        }
    }

    private fun occupation(occupation: String?, builder: StringBuilder) {
        if (occupation != null) {
            builder.append("1 ").append(OCCUPATION).append(" ").append(occupation).append('\n')
        }
    }

    private fun note(note: String?, builder: StringBuilder) {
        if (note != null) {
            val paragraphs = note.split('\n')
            for ((index, paragraph) in paragraphs.withIndex()) {
                if (index == 0) {
                    builder.append("1 ").append(NOTE).append(" ").append(paragraph).append('\n')
                } else {
                    builder.append("2 ").append(CONTINUE).append(" ").append(paragraph).append('\n')
                }
            }
        }
    }

    private fun events(events: MutableSet<out LifeEvent>, builder: StringBuilder) {
        if (events.isNotEmpty()) {
            events.forEach { event(it, builder) }
        }
    }

    private fun event(event: LifeEvent, builder: StringBuilder) {
        builder.append("1 ").append(event.type.getCode()).append('\n')

        if (event.date != null) {
            builder.append("2 ").append(DATE).append(" ")
            if (event.prefix != null && event.prefix != EventPrefix.None) {
                builder.append(event.prefix!!.code).append(' ')
            }
            builder.append(event.date!!.format(datePattern)).append('\n')
        }
        if (event.place != null) {
            builder.append("2 ").append(PLACE).append(" ").append(event.place).append('\n')
        }
        if (event.note != null) {
            val paragraphs = event.note!!.split('\n')
            for ((index, paragraph) in paragraphs.withIndex()) {
                if (index == 0) {
                    builder.append("2 ").append(NOTE).append(" ").append(paragraph).append('\n')
                } else {
                    builder.append("3 ").append(CONTINUE).append(" ").append(paragraph).append('\n')
                }
            }
        }
    }

    private fun children(children: Set<Person>, builder: StringBuilder) {
        children.forEach {
            builder.append("1 ").append(CHILD).append(" @").append(it.id).append("@\n")
        }
    }

    private fun person(person: Person?, type: String, builder: StringBuilder) {
        if (person != null) {
            builder.append("1 ").append(type).append(" @").append(person.id).append("@\n")
        }
    }
}
