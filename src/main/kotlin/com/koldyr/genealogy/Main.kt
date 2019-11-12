package com.koldyr.genealogy

import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.ui.GenealogyApp
import java.io.File
import java.io.IOException

object Main {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val lineage: Lineage
        val fileName = if (args.isEmpty()) null else args[0]
        if (fileName == null) {
            lineage = Lineage(listOf(), setOf())
        } else {
            val file = File(fileName)
            val parser = ImporterFactory.create(file)
            lineage = parser.import(file.toPath())
            println("lineage = $lineage")
        }

        val appWindow = GenealogyApp(lineage, fileName)
        appWindow.isVisible = true
    }
}
