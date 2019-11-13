package com.koldyr.genealogy.importer

import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koldyr.genealogy.model.Lineage
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class JSONImporter : Importer {

    override fun import(file: Path): Lineage {
        return import(Files.newInputStream(file))
    }

    override fun import(input: InputStream): Lineage {
        val lineage = mapper().readValue(input, Lineage::class.java)

        lineage.families.forEach {
            if (it.wife != null) {
                val person = lineage.findPerson(it.wife?.id)
                if (person != null) {
                    it.wife = person
                }
            }

            if (it.husband != null) {
                val person = lineage.findPerson(it.husband?.id)
                if (person != null) {
                    it.husband = person
                }
            }

            for (child in it.children.toSet()) {
                val person = lineage.findPerson(child.id)
                if (person != null) {
                    it.children.remove(child)
                    it.children.add(person)
                }
            }
        }

        return lineage
    }

    private fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        mapper.enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        mapper.disable(FAIL_ON_UNKNOWN_PROPERTIES)
        return mapper
    }
}
