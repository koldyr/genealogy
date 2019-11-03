package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Clan
import java.io.File

/**
 * Description of class Exporter
 * @created: 2019.10.31
 */
interface Exporter {
    fun export(file: File, clan: Clan)
}
