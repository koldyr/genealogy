package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.Persons
import java.io.File
import java.nio.file.Files
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller

class XMLImporter : Importer {

    override fun import(file: File): Collection<Person> {
        val stream = Files.newInputStream(file.toPath())
        return stream.bufferedReader(Charsets.UTF_8).use { reader ->
            val persons: Persons = unmarshaller().unmarshal(reader) as Persons
            return persons.persons
        }
    }

    private fun unmarshaller(): Unmarshaller {
        val jaxbContext = JAXBContext.newInstance(Persons::class.java)
        val jaxbUnMarshaller = jaxbContext.createUnmarshaller()
        return jaxbUnMarshaller
    }
}
