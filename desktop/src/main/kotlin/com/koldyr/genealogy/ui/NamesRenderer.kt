package com.koldyr.genealogy.ui

import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import com.koldyr.genealogy.model.PersonNames

/**
 * Description of class NamesRenderer
 * @created: 2019-10-27
 */
class NamesRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        val name = value as PersonNames
        val nameBuilder = mutableListOf<String>()
        name.first?.let {
            nameBuilder.add(it)
        }
        name.middle?.let {
            nameBuilder.add(it)
        }
        name.last?.let {
            nameBuilder.add(it)
        }
        name.maiden?.let {
            nameBuilder.add("($it)")
        }
        return super.getTableCellRendererComponent(table, nameBuilder.joinToString(" "), isSelected, hasFocus, row, column)
    }
}
