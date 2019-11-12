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
        return mapper().readValue(input, Lineage::class.java)
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
