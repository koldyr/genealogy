package com.koldyr.genealogy.renderer

import java.awt.Component
import java.time.format.DateTimeFormatter
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import com.koldyr.genealogy.model.LifeEvent

/**
 * Description of class NamesRenderer
 * @created: 2019-10-27
 */
class EventsRenderer : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        if (value is LifeEvent) {
            val event: LifeEvent = value;
            val nameBuilder = mutableListOf<String>()
            event.date?.let {
                nameBuilder.add(it.format(DateTimeFormatter.ISO_LOCAL_DATE))
            }
            event.place?.let {
                nameBuilder.add(it)
            }
            return super.getTableCellRendererComponent(table, nameBuilder.joinToString(" "), isSelected, hasFocus, row, column)
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
    }
}