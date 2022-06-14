package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.Importer
import com.koldyr.genealogy.importer.JSONImporter

/**
 * Description of class JSONExporterTest
 *
 * @created: 2019-11-12
 */
class JSONExporterTest : BaseExpImpTest() {

    override fun getImporter(): Importer {
        return JSONImporter()
    }

    override fun getExporter(): Exporter {
        return JSONExporter()
    }
}