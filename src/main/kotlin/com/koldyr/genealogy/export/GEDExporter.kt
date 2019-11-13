package com.koldyr.genealogy.export

import com.koldyr.genealogy.model.Lineage
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JFrame
import javax.swing.JOptionPane

/**
 * Description of class GEDExporter
 * @created: 2019.10.31
 */
class GEDExporter : Exporter {

    override fun export(lineage: Lineage, output: OutputStream) {
        output.bufferedWriter(Charsets.UTF_8).use { writer ->
            JOptionPane.showMessageDialog(JFrame.getFrames()[0], "Not implemented")
        }
    }

    override fun export(lineage: Lineage, file: Path) {
        export(lineage, Files.newOutputStream(file))
    }
}
