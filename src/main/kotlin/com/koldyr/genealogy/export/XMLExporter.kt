package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.Persons
import java.io.File
import java.nio.file.Files
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

/**
 * Description of class XMLExporter
 * @created: 2019.10.31
 */
class XMLExporter: Exporter {

    override fun export(file: File, persons: Collection<Person>) {
        val stream = Files.newOutputStream(file.toPath())
        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
            marshaller().marshal(Persons(persons), writer)
        }
    }

    private fun marshaller(): Marshaller {
        val jaxbContext = JAXBContext.newInstance(Persons::class.java)
        val jaxbMarshaller = jaxbContext.createMarshaller()
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        return jaxbMarshaller
    }
}
