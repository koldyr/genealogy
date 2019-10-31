package com.koldyr.genealogy.export

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
            TODO("not implemented")
        }
    }
}
