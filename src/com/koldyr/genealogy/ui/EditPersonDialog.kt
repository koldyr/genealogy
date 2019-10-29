package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.time.format.DateTimeFormatter
import javax.swing.*
import javax.swing.border.EmptyBorder


/**
 * Description of class EditPersonDialog
 * @created: 2019-10-27
 */
class EditPersonDialog(owner: Frame?, private var person: Person) : JDialog(owner, "Edit Person", true) {
    init {
        val lblId = JLabel("Id:")
        val txtId = JTextField(person.id.toString())
        txtId.isEditable = false

        val name: PersonNames = person.name!!
        val lblName = JLabel("Name:")
        val txtName = JTextField(name.name)
        val txtMiddle = JTextField(name.middle)
        val txtLast = JTextField(name.last)
        val txtMaiden = JTextField(name.maiden)

        val lblSex = JLabel("Sex:")
        val cmbSex = JComboBox(Sex.values())
        cmbSex.selectedItem = person.sex

        val lblBirth = JLabel("Birth:")
        val txtBirth = JTextField()
        val txtBirthPlace = JTextField()
        if (person.birth != null) {
            val birth: LifeEvent = person.birth!!
            if (birth.date != null) {
                txtBirth.text = birth.date!!.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
            txtBirthPlace.text = birth.place
        }

        val lblDeath = JLabel("Death:")
        val txtDeath = JTextField()
        val txtDeathPlace = JTextField()

        if (person.death != null) {
            val death: LifeEvent = person.death!!
            if (death.date != null) {
                txtDeath.text = death.date!!.format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
            txtDeathPlace.text = death.place
        }

        val lblPlace = JLabel("Place:")
        val txtPlace = JTextField(person.place)

        val lblOccupation = JLabel("Occupation:")
        val txtOccupation = JTextField(person.occupation)

        val lblNote = JLabel("Note:")
        val txtNote = JTextArea(person.note, 3, 20)

        rootPane.border = EmptyBorder(10, 10, 10, 10)

        val pnlContent = JPanel(GridBagLayout())
        pnlContent.add(lblId, GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtId, GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        pnlContent.add(lblName, GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtName, GridBagConstraints(1, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtMiddle, GridBagConstraints(2, 1, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        if (person.sex == Sex.FEMALE) {
            pnlContent.add(txtMaiden, GridBagConstraints(3, 1, 1, 1, 0.5, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        }

        pnlContent.add(lblSex, GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(cmbSex, GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        pnlContent.add(lblBirth, GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtBirth, GridBagConstraints(1, 3, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtBirthPlace, GridBagConstraints(2, 3, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        pnlContent.add(lblDeath, GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtDeath, GridBagConstraints(1, 4, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtDeathPlace, GridBagConstraints(2, 4, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        pnlContent.add(lblPlace, GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtPlace, GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        pnlContent.add(lblOccupation, GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtOccupation, GridBagConstraints(1, 6, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        pnlContent.add(lblNote, GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(JScrollPane(txtNote), GridBagConstraints(1, 7, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        val okAction = object : AbstractAction("Ok") {
            init {
                putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ENTER)
            }

            override fun actionPerformed(evt: ActionEvent) {
                save()
                close()
            }
        }

        val btnOk = JButton(okAction)
        btnOk.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ok")
        btnOk.actionMap.put("ok", okAction)

        val cancelAction = object : AbstractAction("Cancel") {
            init {
                putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ESCAPE)
            }

            override fun actionPerformed(evt: ActionEvent) {
                close()
            }
        }

        val btnCancel = JButton(cancelAction)
        btnCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel")
        btnCancel.actionMap.put("cancel", cancelAction)

        val pnlButtons = JPanel(FlowLayout(FlowLayout.TRAILING))
        pnlButtons.add(btnOk)
        pnlButtons.add(btnCancel)

        contentPane.add(pnlContent)
        contentPane.add(pnlButtons, BorderLayout.SOUTH)

        rootPane.defaultButton = btnOk

        pack()
        setLocationRelativeTo(null)
    }

    fun getPerson(): Person? {
        return person
    }

    private fun save() {

    }

    private fun close() {
        isVisible = false
    }
}
