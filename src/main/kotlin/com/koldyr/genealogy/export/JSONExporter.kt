package com.koldyr.genealogy.export

import com.fasterxml.jackson.annotation.JsonInclude.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koldyr.genealogy.model.Lineage
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Description of class JSONExporter
 * @created: 2019.10.31
 */
class JSONExporter : Exporter {

    override fun export(lineage: Lineage, output: OutputStream) {
        mapper().writeValue(output, lineage)
    }

    override fun export(lineage: Lineage, file: Path) {
        export(lineage, Files.newOutputStream(file))
    }

    private fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        mapper.setSerializationInclusion(Include.NON_EMPTY)
        mapper.enable(INDENT_OUTPUT)
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS)
        return mapper
    }
}
