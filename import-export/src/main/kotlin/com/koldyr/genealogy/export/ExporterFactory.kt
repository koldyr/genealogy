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
        fun create(type: String?): Exporter {
            return if (type == null || type.contains("ged")) GEDExporter()
            else if (type.contains("json")) JSONExporter()
            else CSVExporter()
        }
    }
}
