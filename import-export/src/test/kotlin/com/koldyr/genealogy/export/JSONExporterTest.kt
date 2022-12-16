package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.importer.ImporterFactory

/**
 * Description of class JSONExporterTest
 *
 * @created: 2019-11-12
 */
class JSONExporterTest : BaseExpImpTest() {

    override fun getImporter(): Importer = ImporterFactory.create("json")

    override fun getExporter(): Exporter = ExporterFactory.create("json")
}
