package com.koldyr.genealogy

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.parser.FamilyTreeDataParser
import com.koldyr.genealogy.ui.GenealogyApp
import java.io.IOException

object Main {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val fileName = if (args.isEmpty()) null else args[0]

        val persons: Collection<Person>

        if (fileName == null) {
            persons = listOf()
        } else {
            val parser = FamilyTreeDataParser()
            persons = parser.parse(fileName)
            println("persons = ${persons}")
        }

        val appWindow = GenealogyApp(persons, fileName)
        appWindow.isVisible = true
    }
}
