package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.importer.ImporterFactory

/**
 * Description of class CSVExporterTest
 *
 * @created: 2019-11-12
 */
class CSVExporterTest : BaseExpImpTest() {

    override fun getImporter(): Importer = ImporterFactory.create("csv")

    override fun getExporter(): Exporter = ExporterFactory.create("csv")
}
