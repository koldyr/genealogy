package com.koldyr.genealogy

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.parser.FamilyTreeDataParser
import com.koldyr.genealogy.ui.GenealogyApp
import java.io.IOException

object Main {

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = FamilyTreeDataParser()
        val families: Set<Family> = parser.parse("./galitskie.ged")
        println("families = ${families}")

        val appWindow = GenealogyApp(families)
        appWindow.isVisible = true
    }
}
