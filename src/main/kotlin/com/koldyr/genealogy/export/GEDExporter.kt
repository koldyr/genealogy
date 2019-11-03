package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Clan
import java.io.File
import java.nio.file.Files
import javax.swing.JFrame
import javax.swing.JOptionPane

/**
 * Description of class GEDExporter
 * @created: 2019.10.31
 */
class GEDExporter: Exporter {

    override fun export(file: File, clan: Clan) {
        val stream = Files.newOutputStream(file.toPath())
        stream.bufferedWriter(Charsets.UTF_8).use { writer ->
            JOptionPane.showMessageDialog(JFrame.getFrames()[0], "Not implemented")
        }
    }
}
