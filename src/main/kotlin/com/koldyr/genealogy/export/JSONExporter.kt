package com.koldyr.genealogy.export

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
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
            val objectMapper = ObjectMapper()
            objectMapper.serializationConfig.with(SerializationFeature.INDENT_OUTPUT)
            objectMapper.serializationConfig.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.dateFormat = StdDateFormat()
            objectMapper.writeValue(writer, persons)
        }
    }
}
