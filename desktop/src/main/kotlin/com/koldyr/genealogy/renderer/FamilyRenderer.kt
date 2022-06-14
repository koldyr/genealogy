package com.koldyr.genealogy.renderer

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Lineage

/**
 * Description of class FamilyRenderer
 * @created: 2019-11-07
 */
class FamilyRenderer(val lineage: Lineage) : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)

        val family = value as Family?
        family?.let {
            val familyName = getFamilyName(family)
            this.text = "${family.id ?: "-"} $familyName"
        }

        return this
    }

    private fun getFamilyName(family: Family): String {
        val person = family.husband ?: family.wife ?: return ""
        val firstName = person.name?.first ?: ""
        val lastName = person.name?.last ?: ""
        return "$lastName $firstName"
    }
}