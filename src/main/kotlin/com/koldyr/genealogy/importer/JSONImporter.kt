package com.koldyr.genealogy.importer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.koldyr.genealogy.model.Person
import java.io.File
import java.nio.file.Files

class JSONImporter : Importer {

    override fun import(file: File): Collection<Person> {
        val stream = Files.newInputStream(file.toPath())
        return stream.bufferedReader(Charsets.UTF_8).use { reader ->
            val mapper = mapper()
            val type = mapper.typeFactory.constructCollectionType(ArrayList::class.java, Person::class.java)
            return mapper.readValue(reader, type)
        }
    }

    private fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.serializationConfig.with(SerializationFeature.INDENT_OUTPUT)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModule(JavaTimeModule())
        return mapper
    }
}
