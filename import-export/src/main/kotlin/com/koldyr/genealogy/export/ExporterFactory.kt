package com.koldyr.genealogy.export

/**
 * Description of class ExporterFactory
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
class ExporterFactory {

    companion object {
        @JvmStatic
        fun create(type: String): Exporter {
            return if (type.contains("json")) JSONExporter()
            else if (type.contains("ged")) GEDExporter()
            else CSVExporter()
        }
    }
}
