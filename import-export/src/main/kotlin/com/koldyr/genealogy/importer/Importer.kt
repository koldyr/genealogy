package com.koldyr.genealogy.importer

import java.io.InputStream
import java.nio.file.Path
import com.koldyr.genealogy.model.Lineage

/**
 * Description of class Importer
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
interface Importer {

    fun import(file: Path): Lineage

    fun import(input: InputStream): Lineage
}
