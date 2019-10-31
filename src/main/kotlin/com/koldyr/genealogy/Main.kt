package com.koldyr.genealogy

import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.ui.GenealogyApp
import java.io.File
import java.io.IOException

object Main {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val persons: Collection<Person>
        val fileName = if (args.isEmpty()) null else args[0]
        if (fileName == null) {
            persons = listOf()
        } else {
            val file = File(fileName)
            val parser = ImporterFactory.create(file)
            persons = parser.import(file)
            println("persons = $persons")
        }

        val appWindow = GenealogyApp(persons, fileName)
        appWindow.isVisible = true
    }
}
