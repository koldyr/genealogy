package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.importer.ImporterFactory

/**
 * Description of class GEDExporterTest
 *
 * @created: 2019-11-12
 */
class GEDExporterTest : BaseExpImpTest() {

    override fun getImporter(): Importer = ImporterFactory.create("ged")

    override fun getExporter(): Exporter = ExporterFactory.create("ged")
}
