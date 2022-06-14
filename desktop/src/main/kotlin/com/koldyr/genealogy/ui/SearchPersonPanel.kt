package com.koldyr.genealogy.ui

import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Description of class SearchPersonPanel
 * @created: 2019-11-07
 */
class SearchPersonPanel : JPanel(GridBagLayout()) {
    private val txtInput = JTextField()
    private val chbMatchCase = JCheckBox("Match Case")
    private val chbWholeWord = JCheckBox("Whole Word")
    private val chbClear = JCheckBox("Clear Search")

    init {
        var rowIndex = 0
        add(txtInput, GridBagConstraints(0, rowIndex, 3, 1, 1.0, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        rowIndex++
        add(chbMatchCase, GridBagConstraints(0, rowIndex, 1, 1, 1.0, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 0, 5), 0, 0))
        add(chbWholeWord, GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
        add(chbClear, GridBagConstraints(2, rowIndex, 1, 1, 1.0, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 0, 0), 0, 0))
    }

    fun getSearch(): SearchData? {
        if (chbClear.isSelected) {
            return null
        }
        
        return SearchData(txtInput.text, chbMatchCase.isSelected, chbWholeWord.isSelected)
    }
}
