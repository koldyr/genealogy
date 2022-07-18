package com.koldyr.genealogy.export

import java.io.OutputStream
import java.nio.file.Files.*
import java.nio.file.Path
import java.time.format.DateTimeFormatter
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

/**
 * Description of class GEDExporter
 * @created: 2019.10.31
 */
class GEDExporter : Exporter {
    private val datePattern = DateTimeFormatter.ofPattern("d MMM yyyy")

    private val header =
            "0 HEAD\n" +
            "1 SOUR ALTREE\n" +
            "2 NAME Древо Жизни\n" +
            "2 VERS 2.31\n" +
            "2 CORP Genery Software\n" +
            "3 ADDR www.genery.com\n" +
            "1 CHAR UTF-8\n" +
            "1 DATE DD MMM YYYY\n" +
            "1 GEDC\n" +
            "2 VERS 5.5\n" +
            "2 FORM Lineage-Linked\n" +
            "1 SUBM @SUBM@\n" +
            "0 @SUBM@ SUBM\n" +
            "1 NAME unknown\n"

    private val footer = "0 TRLR\n"

    override fun export(lineage: Lineage, file: Path) {
        export(lineage, newOutputStream(file))
    }

    override fun export(lineage: Lineage, output: OutputStream) {
        output.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write(header)

            lineage.persons.forEach {
                writer.write(person(it))
            }

            lineage.families.forEach {
                writer.write(family(it))
            }

            writer.write(footer)
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
        person.familyId?.let {
            builder.append("1 ").append(OWN_FAMILY).append(" @F").append(it).append("@\n")
        }
        person.parentFamilyId?.let {
            builder.append("1 ").append(PARENT_FAMILY).append(" @F").append(it).append("@\n")
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

    private fun id(id: Long, person: Boolean, builder: StringBuilder) {
        if (person) {
            builder.append("0 @").append(id).append("@ ").append(PERSON).append('\n')
        } else {
            builder.append("0 @F").append(id).append("@ ").append(FAMILY).append('\n')
        }
    }

    private fun name(names: PersonNames?, builder: StringBuilder) {
        if (names != null) {
            val nameBuilder = StringBuilder()

            names.first?.let {
                nameBuilder.append(it)
            }
            names.middle?.let {
                if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')
                nameBuilder.append(it)
            }
            if (names.last != null || names.maiden != null) {
                if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')

                nameBuilder.append('/')

                names.last?.let {
                    nameBuilder.append(it)
                }

                names.maiden?.let {
                    if (names.last != null) nameBuilder.append(' ')
                    nameBuilder.append('(').append(it).append(')')
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
        place?.let {
            builder.append("1 ").append(RESIDENCE).append(" ").append(it).append('\n')
        }
    }

    private fun occupation(occupation: String?, builder: StringBuilder) {
        occupation?.let {
            builder.append("1 ").append(OCCUPATION).append(" ").append(it).append('\n')
        }
    }

    private fun note(note: String?, builder: StringBuilder) {
        note?.let {
            val paragraphs = it.split('\n')
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

        event.date?.let {
            builder.append("2 ").append(DATE).append(" ")
            if (event.prefix != null && event.prefix != EventPrefix.None) {
                builder.append(event.prefix!!.code).append(' ')
            }
            builder.append(it.format(datePattern)).append('\n')
        }
        event.place?.let {
            builder.append("2 ").append(PLACE).append(" ").append(it).append('\n')
        }
        event.note?.let {
            val paragraphs = it.split('\n')
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
        person?.let {
            builder.append("1 ").append(type).append(" @").append(it.id).append("@\n")
        }
    }
}
