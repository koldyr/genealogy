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
        val nameBuilder: StringBuilder = StringBuilder(name.name)
        if (name.middle != null) {
            nameBuilder.append(' ').append(name.middle)
        }
        if (name.last != null) {
            nameBuilder.append(' ').append(name.last)
        }
        if (name.maiden != null) {
            nameBuilder.append(" (").append(name.maiden).append(')')
        }
        return super.getTableCellRendererComponent(table, nameBuilder.toString(), isSelected, hasFocus, row, column)
    }
}
