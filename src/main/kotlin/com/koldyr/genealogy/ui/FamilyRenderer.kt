package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Lineage
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

/**
 * Description of class FamilyRenderer
 * @created: 2019-11-07
 */
class FamilyRenderer(val lineage: Lineage) : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        val family = value as Family?
        val lastName = getFamilyName(family)
        this.text = "${family?.id ?: "-"} $lastName"

        return this
    }

    private fun getFamilyName(family: Family?): String {
        if (family == null) {
            return ""
        }
        val person = family.husband ?: family.wife

        return if (person == null) "" else person.name?.last ?: ""
    }
}
