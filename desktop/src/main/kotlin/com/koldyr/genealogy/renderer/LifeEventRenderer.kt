package com.koldyr.genealogy.renderer

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import org.apache.commons.lang3.StringUtils
import com.koldyr.genealogy.model.LifeEvent

class LifeEventRenderer : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        if (value is LifeEvent) {
            text = "${value.prefix ?: StringUtils.EMPTY} ${value.date ?: "?"} ${value.type} in ${value.place ?: "?"}"
        }

        return this
    }
}