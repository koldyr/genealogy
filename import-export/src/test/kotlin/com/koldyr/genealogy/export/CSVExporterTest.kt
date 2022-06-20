package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.CSVImporter
import com.koldyr.genealogy.importer.Importer

/**
 * Description of class CSVExporterTest
 *
 * @created: 2019-11-12
 */
class CSVExporterTest : BaseExpImpTest() {

    override fun getImporter(): Importer {
        return CSVImporter()
    }

    override fun getExporter(): Exporter {
        return CSVExporter()
    }
}