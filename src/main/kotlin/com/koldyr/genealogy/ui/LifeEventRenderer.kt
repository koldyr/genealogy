package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import org.apache.commons.lang3.StringUtils.*
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class LifeEventRenderer : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        if (value is LifeEvent) {
            text = "${value.prefix ?: EMPTY} ${value.date ?: "?"} ${value.type} in ${value.place ?: "?"}"
        }

        return this
    }
}
