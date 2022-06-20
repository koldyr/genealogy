package com.koldyr.genealogy.ui

import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.*
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import org.jdatepicker.JDatePicker
import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.LifeEvent

/**
 * Description of class LifeEventEditPanel
 * @created: 2019-11-07
 */
class LifeEventEditPanel: JPanel {
    private val cmbType: JComboBox<EventType>
    private val cmbPrefix: JComboBox<EventPrefix>
    private val dateModel: LocalDateModel
    private val txtPlace: JTextField
    private val txtNote: JTextArea

    private var event: LifeEvent?

    constructor(event: LifeEvent?) : super(GridBagLayout()) {
        this.event = event

        val lblType = JLabel("Type:")
        cmbType = JComboBox(EventType.values())

        val lblDate = JLabel("Date:")
        cmbPrefix = JComboBox(EventPrefix.values())
        dateModel = LocalDateModel()
        val dpBirth = JDatePicker(dateModel, "yyyy MMM dd")

        val lblPlace = JLabel("Place:")
        txtPlace = JTextField()

        val lblNote = JLabel("Note:")
        txtNote = JTextArea(3, 5)

        event?.let {
            cmbType.selectedItem = event.type
            cmbPrefix.selectedItem = event.prefix
            dateModel.value = event.date
            txtPlace.text = event.place
            txtNote.text = event.note
        }

        var rowIndex = 0
        add(lblType, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                WEST, NONE, Insets(0, 0, 5, 5), 0, 0))
        add(cmbType, GridBagConstraints(1, rowIndex, 2, 1, 0.5, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        rowIndex++
        add(lblDate, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                WEST, NONE, Insets(0, 0, 5, 5), 0, 0))
        add(cmbPrefix, GridBagConstraints(1, rowIndex, 1, 1, 0.5, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        add(dpBirth, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        add(lblPlace, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                WEST, NONE, Insets(0, 0, 5, 5), 0, 0))
        add(txtPlace, GridBagConstraints(1, rowIndex, 2, 1, 0.5, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        add(lblNote, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                WEST, NONE, Insets(0, 0, 5, 5), 0, 0))
        add(JScrollPane(txtNote), GridBagConstraints(1, rowIndex, 2, 1, 0.5, 0.0,
                WEST, HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
    }

    fun getEvent(): LifeEvent? {
        if (dateModel.value == null) {
            return null
        }

        val place = txtPlace.text.takeIf { it.isNotEmpty() }
        val note = txtNote.text.takeIf { it.isNotEmpty() }

        if (this.event == null) {
            val type = cmbType.selectedItem as EventType
            val prefix = getPrefix()
            return LifeEvent(type, prefix, dateModel.value, place, note)
        }

        val changed = this.event as LifeEvent
        changed.type = cmbType.selectedItem as EventType
        changed.prefix = getPrefix()
        changed.date = dateModel.value
        changed.place = place
        changed.note = note

        return changed
    }

    private fun getPrefix(): EventPrefix? {
        val prefix = cmbPrefix.selectedItem as EventPrefix?
        return if (prefix == EventPrefix.None) null else prefix
    }
}
