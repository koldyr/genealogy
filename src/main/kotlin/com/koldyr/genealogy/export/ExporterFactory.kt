package com.koldyr.genealogy.export

/**
 * Description of class ExporterFactory
 * @created: 2019.10.31
 */
class ExporterFactory {

    companion object {
        @JvmStatic
        fun create(type: String): Exporter {
            return when (type) {
                "json" -> JSONExporter()
                "ged" -> GEDExporter()
                else -> CSVExporter()
            }
        }
    }
}
