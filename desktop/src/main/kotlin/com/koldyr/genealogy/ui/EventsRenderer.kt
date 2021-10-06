package com.koldyr.genealogy.ui

import java.awt.Component
import java.time.format.DateTimeFormatter
import com.koldyr.genealogy.model.LifeEvent
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

/**
 * Description of class NamesRenderer
 * @created: 2019-10-27
 */
class EventsRenderer: DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        if (value is LifeEvent) {
            val event: LifeEvent = value;
            val nameBuilder: StringBuilder = StringBuilder()
            if (event.date != null) {
                nameBuilder.append(event.date!!.format(DateTimeFormatter.ISO_LOCAL_DATE))
            }
            if (event.place != null) {
                nameBuilder.append(' ').append(event.place)
            }
            return super.getTableCellRendererComponent(table, nameBuilder.toString(), isSelected, hasFocus, row, column)
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
    }
}
