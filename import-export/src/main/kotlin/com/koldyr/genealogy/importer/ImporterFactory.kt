package com.koldyr.genealogy.importer

import java.io.File
import java.nio.file.InvalidPathException
import com.koldyr.genealogy.export.UnsupportedExportFormatException

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

        throw InvalidPathException(file.toString(), "Cannot import dir")
    }

    @JvmStatic
    fun create(dataType: String): Importer {
        val type = dataType.lowercase()
        return if (type.contains("json")) JSONImporter()
        else if (type.contains("ged")) GEDImporter()
        else if (type.contains("csv")) CSVImporter()
        else throw UnsupportedExportFormatException("Cannot import from $dataType")
    }
}
