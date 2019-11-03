package com.koldyr.genealogy.importer

import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koldyr.genealogy.model.Clan
import java.io.File
import java.nio.file.Files

class XMLImporter : Importer {

    override fun import(file: File): Clan {
        val stream = Files.newInputStream(file.toPath())
        val clan: Clan = stream.bufferedReader(Charsets.UTF_8).use {
            reader -> mapper().readValue(reader, Clan::class.java)
        }
        return clan
    }

    private fun mapper(): XmlMapper {
        val mapper = XmlMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.registerModule(KotlinModule())
        mapper.enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        mapper.disable(FAIL_ON_UNKNOWN_PROPERTIES)
        return mapper
    }
}
