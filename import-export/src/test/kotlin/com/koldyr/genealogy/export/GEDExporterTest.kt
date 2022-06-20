package com.koldyr.genealogy.export

import com.koldyr.genealogy.importer.GEDImporter
import com.koldyr.genealogy.importer.Importer

/**
 * Description of class GEDExporterTest
 *
 * @created: 2019-11-12
 */
class GEDExporterTest : BaseExpImpTest() {

    override fun getImporter(): Importer {
        return GEDImporter()
    }

    override fun getExporter(): Exporter {
        return GEDExporter()
    }
}