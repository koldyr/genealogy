package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.PersonNames
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

/**
 * Description of class NamesRenderer
 * @created: 2019-10-27
 */
class NamesRenderer: DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        val name: PersonNames = value as PersonNames;
        val nameBuilder: StringBuilder = StringBuilder()
        if (name.first != null) {
            nameBuilder.append(name.first)
        }
        if (name.middle != null) {
            if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')
            nameBuilder.append(name.middle)
        }
        if (name.last != null) {
            if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')
            nameBuilder.append(name.last)
        }
        if (name.maiden != null) {
            if (nameBuilder.isNotEmpty()) nameBuilder.append(' ')
            nameBuilder.append('(').append(name.maiden).append(')')
        }
        return super.getTableCellRendererComponent(table, nameBuilder.toString(), isSelected, hasFocus, row, column)
    }
}
