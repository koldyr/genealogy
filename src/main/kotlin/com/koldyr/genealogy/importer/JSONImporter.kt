package com.koldyr.genealogy.importer

import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.koldyr.genealogy.model.Person
import java.io.File
import java.nio.file.Files

class JSONImporter : Importer {

    override fun import(file: File): Collection<Person> {
        val mapper = mapper()
        val type = mapper.typeFactory.constructCollectionType(ArrayList::class.java, Person::class.java)

        val stream = Files.newInputStream(file.toPath())
        return stream.bufferedReader(Charsets.UTF_8).use {
            reader -> mapper.readValue(reader, type)
        }
    }

    private fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        mapper.disable(FAIL_ON_UNKNOWN_PROPERTIES)
        mapper.registerModule(JavaTimeModule())
        return mapper
    }
}
