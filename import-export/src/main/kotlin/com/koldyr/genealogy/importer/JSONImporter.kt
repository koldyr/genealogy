package com.koldyr.genealogy.importer

import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import com.fasterxml.jackson.databind.DeserializationFeature.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.koldyr.genealogy.model.Lineage

/**
 * Description of the JSONImporter class
 *
 * @author d.halitski@gmail.com
 * @created 2019-10-31
 */
class JSONImporter : Importer {

    override fun import(file: Path): Lineage {
        return Files.newInputStream(file).use {
            import(it)
        }
    }

    override fun import(input: InputStream): Lineage {
        val lineage = mapper().readValue(input, Lineage::class.java)
        return Lineage(lineage.persons, lineage.families, true)
    }

    private fun mapper(): ObjectMapper {
        return JsonMapper.builder()
            .addModules(JavaTimeModule(), KotlinModule.Builder().build())
            .enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .build()
    }
}
