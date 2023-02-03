package com.koldyr.genealogy.importer

import java.io.File

/**
 * Description of class ImporterFactory
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
object ImporterFactory {

    fun create(file: File): Importer {
        if (file.isFile) {
            return create(file.extension)
        }

        throw RuntimeException("Cannot import dir")
    }

    @JvmStatic
    fun create(dataType: String): Importer {
        val type = dataType.lowercase()
        return if (type.contains("json")) JSONImporter()
        else if (type.contains("ged")) GEDImporter()
        else if (type.contains("csv")) CSVImporter()
        else throw RuntimeException("Cannot import from $dataType")
    }
}
