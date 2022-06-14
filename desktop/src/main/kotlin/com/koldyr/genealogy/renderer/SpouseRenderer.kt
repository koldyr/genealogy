package com.koldyr.genealogy.renderer

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import com.koldyr.genealogy.model.Person

/**
 * Description of class SpouseRenderer
 * @created: 2022-06-14
 */
class SpouseRenderer : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        val person = value as Person
        person.name?.let {name ->
            val nameBuilder = mutableListOf<String>()
            person.id?.let {
                nameBuilder.add(it.toString())
            }
            name.first?.let {
                nameBuilder.add(it)
            }
            name.last?.let {
                nameBuilder.add(it)
            }
            text = nameBuilder.joinToString(" ")
        }

        return this
    }
}