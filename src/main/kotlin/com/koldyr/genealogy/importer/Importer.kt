package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.Lineage
import java.io.InputStream
import java.nio.file.Path

/**
 * Description of class Importer
 * @created: 2019.10.31
 */
interface Importer {

    fun import(file: Path): Lineage

    fun import(input: InputStream): Lineage
}
