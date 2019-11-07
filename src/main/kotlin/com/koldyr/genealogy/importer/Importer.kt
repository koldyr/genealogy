package com.koldyr.genealogy.importer

import com.koldyr.genealogy.model.Lineage
import java.io.File

/**
 * Description of class Importer
 * @created: 2019.10.31
 */
interface Importer {
    fun import(file: File): Lineage
}
