package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.Persons
import java.io.File
import java.nio.file.Files
import javax.xml.bind.JAXBContext

class XMLImporter : Importer {
    override fun import(file: File): Collection<Person> {
        val jaxbContext = JAXBContext.newInstance(Persons::class.java)
        val jaxbUnMarshaller = jaxbContext.createUnmarshaller()

        val stream = Files.newInputStream(file.toPath())
        return stream.bufferedReader(Charsets.UTF_8).use { reader ->
            val persons: Persons = jaxbUnMarshaller.unmarshal(reader) as Persons
            return persons.persons
        }
    }
}
