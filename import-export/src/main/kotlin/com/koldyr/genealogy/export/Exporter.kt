package com.koldyr.genealogy.export

import java.io.OutputStream
import java.nio.file.Path
import com.koldyr.genealogy.model.Lineage

/**
 * Description of class Exporter
 *
 * @author d.halitski@gmail.com
 * @created: 2019.10.31
 */
interface Exporter {
    fun export(lineage: Lineage, file: Path)

    fun export(lineage: Lineage, output: OutputStream)
}
