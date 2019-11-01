package com.koldyr.genealogy.export

import com.fasterxml.jackson.annotation.JsonInclude.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.*
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.koldyr.genealogy.model.Person
import java.io.File
import java.nio.file.Files

/**
 * Description of class JSONExporter
 * @created: 2019.10.31
 */
class JSONExporter: Exporter {

    override fun export(file: File, persons: Collection<Person>) {
        val stream = Files.newOutputStream(file.toPath())
        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
            mapper().writeValue(writer, persons)
        }
    }

    private fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.serializationConfig.with(INDENT_OUTPUT)
        mapper.setSerializationInclusion(Include.NON_NULL)
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModule(JavaTimeModule())
        return mapper
    }
}
