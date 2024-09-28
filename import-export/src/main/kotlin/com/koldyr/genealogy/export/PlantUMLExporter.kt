package com.koldyr.genealogy.export

import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import org.apache.commons.lang3.StringUtils.EMPTY
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames

/**
 * Description of class PlantUMLExporter
 *
 * @author d.halitski@gmail.com
 * @created: 2024.09.28
 */
class PlantUMLExporter : Exporter {

    override fun export(lineage: Lineage, file: Path) {
        export(lineage, Files.newOutputStream(file))
    }

    override fun export(lineage: Lineage, output: OutputStream) {
        output.bufferedWriter(Charsets.UTF_8).use { writer ->
            writer.write("@startuml")
            writer.newLine()

            lineage.persons.forEach { person ->
                writer.write(person(person))
                writer.newLine()
            }
            writer.newLine()
            lineage.families.forEach { family ->
                writer.write(family(family))
                writer.newLine()
            }

            writer.write("@enduml")
        }
    }

    private fun person(it: Person): String {
        return "actor \"${personNames(it.name)}\" as p${it.id} #${if (it.gender == Gender.MALE) "red" else "blue"}"
    }

    private fun family(it: Family): String {
        val family = StringBuilder()
        family.append("p${it.wife?.id} . p${it.husband?.id}")
        it.children.forEach { child ->
            family.append('\n')
            family.append("p${it.husband?.id} -- p${child.id} ")
        }
        return family.toString()
    }

    private fun personNames(name: PersonNames?): String {
        if (name == null) {
            return EMPTY
        }

        val value = StringBuilder()
        value.append(name.first)
        value.append(" ")
        value.append(name.last ?: EMPTY)

        name.maiden?.also {
            value.append(" (").append(it).append(')')
        }
        return value.toString()
    }
}
