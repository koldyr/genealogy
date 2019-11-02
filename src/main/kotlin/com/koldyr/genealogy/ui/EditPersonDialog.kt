package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import org.apache.commons.lang3.StringUtils.*
import org.jdatepicker.JDatePicker
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.time.LocalDate
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.border.EmptyBorder

/**
 * Description of class EditPersonDialog
 * @created: 2019-10-27
 */
class EditPersonDialog : JDialog {
    private var person: Person
    private var modalResult: Boolean = false

    private val txtName: JTextField
    private val txtMiddle: JTextField
    private val txtLast: JTextField
    private val txtMaiden: JTextField
    private val cmbSex: JComboBox<Sex>
    private val birthModel: LocalDateModel
    private val txtBirthPlace: JTextField
    private val deathModel: LocalDateModel
    private val txtDeathPlace: JTextField
    private val txtPlace: JTextField
    private val txtOccupation: JTextField
    private val txtNote: JTextArea

    constructor(owner: Frame?, person: Person) : super(owner, "Edit Person", true) {
        this.person = person

        val lblId = JLabel("Id:")
        val txtId = JTextField(person.id.toString())
        txtId.isEditable = false

        val name: PersonNames = person.name ?: PersonNames("", "", "", "")
        val lblName = JLabel("Name/Middle:")
        txtName = JTextField(name.name)
        txtMiddle = JTextField(name.middle)
        txtMiddle.preferredSize = Dimension(200, 20)

        val lblLast = JLabel("Last/Maiden:")
        txtLast = JTextField(name.last)
        txtMaiden = JTextField(name.maiden)

        val lblSex = JLabel("Sex:")
        cmbSex = JComboBox(Sex.values())
        cmbSex.addActionListener {
            txtMaiden.isVisible = cmbSex.selectedItem == Sex.FEMALE
        }

        val lblBirth = JLabel("Birth:")

        birthModel = LocalDateModel()
        val dpBirth = JDatePicker(birthModel, "yyyy MMM dd")
        txtBirthPlace = JTextField()

        if (person.birth != null) {
            val birth: LifeEvent = person.birth!!
            birthModel.value = birth.date
            txtBirthPlace.text = birth.place
        }

        val lblDeath = JLabel("Death:")
        deathModel = LocalDateModel()
        val dpDeath = JDatePicker(deathModel, "yyyy MMM dd")
        txtDeathPlace = JTextField()

        if (person.death != null) {
            val death: LifeEvent = person.death!!
            deathModel.value = death.date
            txtDeathPlace.text = death.place
        }

        val lblPlace = JLabel("Place:")
        txtPlace = JTextField(person.place)

        val lblOccupation = JLabel("Occupation:")
        txtOccupation = JTextField(person.occupation)

        val lblNote = JLabel("Note:")
        txtNote = JTextArea(person.note, 4, 20)

        rootPane.border = EmptyBorder(10, 10, 10, 10)

        var rowIndex = 0
        val pnlContent = JPanel(GridBagLayout())
        pnlContent.add(lblId, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtId, GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblName, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtName, GridBagConstraints(1, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtMiddle, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblLast, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtLast, GridBagConstraints(1, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtMaiden, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblSex, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(cmbSex, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblBirth, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(dpBirth, GridBagConstraints(1, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtBirthPlace, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblDeath, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(dpDeath, GridBagConstraints(1, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtDeathPlace, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblPlace, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtPlace, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblOccupation, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtOccupation, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblNote, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(JScrollPane(txtNote), GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        cmbSex.selectedItem = person.sex
        val pnlButtons = createButtonsPanel()

        contentPane.add(pnlContent)
        contentPane.add(pnlButtons, BorderLayout.SOUTH)

        pack()
        setLocationRelativeTo(null)
    }

    fun getPerson(): Person {
        val name: String = txtName.text
        val middle: String? = defaultIfEmpty(txtMiddle.text, null)
        val last: String? = defaultIfEmpty(txtLast.text, null)
        val maiden: String? = defaultIfEmpty(txtMaiden.text, null)
        person.name = PersonNames(name, middle, last, maiden)

        val birthDate: LocalDate? = birthModel.value
        val birthPlace: String? = defaultIfEmpty(txtBirthPlace.text, null)
        person.birth = LifeEvent(birthDate, birthPlace)

        val deathDate: LocalDate? = deathModel.value
        val deathPlace: String? = defaultIfEmpty(txtDeathPlace.text, null)
        person.death = LifeEvent(deathDate, deathPlace)

        person.sex = cmbSex.selectedItem as Sex
        person.place = defaultIfEmpty(txtPlace.text, null)
        person.occupation = defaultIfEmpty(txtOccupation.text, null)
        person.note = defaultIfEmpty(txtNote.text, null)

        return person
    }

    fun getModalResult(): Boolean {
        return modalResult
    }

    private fun createButtonsPanel(): JPanel {
        val okAction = object : AbstractAction("Ok") {
            init {
                putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ENTER)
            }

            override fun actionPerformed(evt: ActionEvent) = close(true)
        }

        val btnOk = JButton(okAction)
        btnOk.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok")
        btnOk.actionMap.put("ok", okAction)

        val cancelAction = object : AbstractAction("Cancel") {
            init {
                putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ESCAPE)
            }

            override fun actionPerformed(evt: ActionEvent) = close(false)
        }

        val btnCancel = JButton(cancelAction)
        btnCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel")
        btnCancel.actionMap.put("cancel", cancelAction)

        val pnlButtons = JPanel(FlowLayout(FlowLayout.TRAILING))
        pnlButtons.add(btnOk)
        pnlButtons.add(btnCancel)
        rootPane.defaultButton = btnOk

        return pnlButtons
    }

    private fun close(value: Boolean) {
        modalResult = value
        isVisible = false
    }
}
