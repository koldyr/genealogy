package com.koldyr.genealogy

import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.parser.FamilyTreeDataParser
import com.koldyr.genealogy.ui.GenealogyApp
import java.io.IOException

object Main {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = FamilyTreeDataParser()
        val persons: Collection<Person> = parser.parse(args[0])
        println("persons = ${persons}")

        val appWindow = GenealogyApp(persons)
        appWindow.isVisible = true
    }
}
