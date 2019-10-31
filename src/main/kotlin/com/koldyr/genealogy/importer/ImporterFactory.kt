package com.koldyr.genealogy.importer

import java.io.File

/**
 * Description of class ImporterFactory
 * @created: 2019.10.31
 */
class ImporterFactory {

    companion object {
        @JvmStatic
        fun create(file: File): Importer {
            if (file.isFile) {
                val fileName = file.name
                val i = fileName.lastIndexOf(".")
                val extension = fileName.substring(i + 1)

                return when (extension) {
                    "xml" -> XMLImporter()
                    "json" -> JSONImporter()
                    "ged" -> GEDImporter()
                    else -> CSVImporter()
                }
            }

            throw RuntimeException("Cannot import dir")
        }
    }
}
