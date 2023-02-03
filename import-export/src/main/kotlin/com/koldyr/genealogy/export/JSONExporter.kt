package com.koldyr.genealogy.export

import java.io.OutputStream
import java.nio.file.Files.*
import java.nio.file.Path
import com.fasterxml.jackson.annotation.JsonInclude.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.*
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koldyr.genealogy.model.Lineage

/**
 * Description of class JSONExporter
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
class JSONExporter : Exporter {

    override fun export(lineage: Lineage, output: OutputStream) {
        mapper().writeValue(output, lineage)
    }

    override fun export(lineage: Lineage, file: Path) {
        newOutputStream(file).use {
            export(lineage, it)
        }
    }

    private fun mapper(): ObjectMapper {
        return JsonMapper.builder()
            .addModule(JavaTimeModule())
            .addModule(KotlinModule.Builder().build())
            .serializationInclusion(Include.NON_EMPTY)
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .build()
    }
}
