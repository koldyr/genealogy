package com.koldyr.genealogy.export

import org.junit.jupiter.api.Assertions
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
        Assertions.assertTrue(exporter is JSONExporter)

        exporter = ExporterFactory.create("ged")
        Assertions.assertTrue(exporter is GEDExporter)

        exporter = ExporterFactory.create("csv")
        Assertions.assertTrue(exporter is CSVExporter)

        exporter = ExporterFactory.create("")
        Assertions.assertTrue(exporter is CSVExporter)
    }
}