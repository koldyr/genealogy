package com.koldyr.genealogy.export

import com.fasterxml.jackson.annotation.JsonInclude.Include.*
import com.fasterxml.jackson.databind.SerializationFeature.*
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koldyr.genealogy.model.Clan
import java.io.File
import java.nio.file.Files

/**
 * Description of class XMLExporter
 * @created: 2019.10.31
 */
class XMLExporter : Exporter {

    override fun export(file: File, clan: Clan) {
        val stream = Files.newOutputStream(file.toPath())
        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
            mapper().writeValue(writer, clan)
        }
    }

    private fun mapper(): XmlMapper {
        val mapper = XmlMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        mapper.setSerializationInclusion(NON_EMPTY)
        mapper.enable(INDENT_OUTPUT)
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS)
        return mapper
    }
}
