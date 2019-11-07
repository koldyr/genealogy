package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.EventPrefix
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.LifeEvent
import org.jdatepicker.JDatePicker
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * Description of class LifeEventEditPanel
 * @created: 2019-11-07
 */
class LifeEventEditPanel: JPanel {
    private val cmbType: JComboBox<EventType>
    private val cmbPrefix: JComboBox<EventPrefix>
    private val dateModel: LocalDateModel
    private val txtPlace: JTextField

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

        if (event != null) {
            cmbType.selectedItem = event.type
            cmbPrefix.selectedItem = event.prefix
            dateModel.value = event.date
            txtPlace.text = event.place
        }

        var rowIndex = 0
        add(lblType, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        add(cmbType, GridBagConstraints(1, rowIndex, 2, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))

        rowIndex++
        add(lblDate, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        add(cmbPrefix, GridBagConstraints(1, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 5), 0, 0))
        add(dpBirth, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        add(lblPlace, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        add(txtPlace, GridBagConstraints(1, rowIndex, 2, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
    }

    fun getEvent(): LifeEvent {
        if (this.event == null) {
            val type = cmbType.selectedItem as EventType
            val prefix = getPrefix()
            return LifeEvent(type, dateModel.value, txtPlace.text, prefix)
        }

        val changed: LifeEvent = this.event!!
        changed.type = cmbType.selectedItem as EventType
        changed.prefix = getPrefix()
        changed.date = dateModel.value
        changed.place = txtPlace.text

        return changed
    }

    private fun getPrefix(): EventPrefix? {
        val prefix = cmbPrefix.selectedItem as EventPrefix?
        return if (prefix == EventPrefix.None) null else prefix
    }
}
