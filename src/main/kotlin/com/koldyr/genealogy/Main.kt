package com.koldyr.genealogy

import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.Clan
import com.koldyr.genealogy.ui.GenealogyApp
import java.io.File
import java.io.IOException

object Main {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val clan: Clan
        val fileName = if (args.isEmpty()) null else args[0]
        if (fileName == null) {
            clan = Clan(listOf())
        } else {
            val file = File(fileName)
            val parser = ImporterFactory.create(file)
            clan = parser.import(file)
            println("persons = $clan")
        }

        val appWindow = GenealogyApp(clan, fileName)
        appWindow.isVisible = true
    }
}
