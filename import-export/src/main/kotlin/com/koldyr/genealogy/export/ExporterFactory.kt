package com.koldyr.genealogy.export

/**
 * Description of class ExporterFactory
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
object ExporterFactory {

    fun create(type: String?): Exporter {
        return when {
            type == null || type.contains("ged") -> GEDExporter()
            type.contains("json") -> JSONExporter()
            type.contains("csv") -> CSVExporter()
            else -> throw UnsupportedExportFormatException("Unsupported format $type")
        }
    }
}
