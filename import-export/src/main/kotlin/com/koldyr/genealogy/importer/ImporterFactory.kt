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
                return when (file.extension) {
                    "json" -> JSONImporter()
                    "ged" -> GEDImporter()
                    else -> CSVImporter()
                }
            }

            throw RuntimeException("Cannot import dir")
        }
    }
}
