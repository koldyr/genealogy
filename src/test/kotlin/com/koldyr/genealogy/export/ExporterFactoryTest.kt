package com.koldyr.genealogy.export

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Description of class ExporterFactoryTest
 *
 * @created: 2019-11-12
 */
class ExporterFactoryTest {

    @Test
    fun create() {
        var exporter = ExporterFactory.create("json")
        assertTrue(exporter is JSONExporter)

        exporter = ExporterFactory.create("ged")
        assertTrue(exporter is GEDExporter)

        exporter = ExporterFactory.create("csv")
        assertTrue(exporter is CSVExporter)

        exporter = ExporterFactory.create("")
        assertTrue(exporter is CSVExporter)
    }
}
