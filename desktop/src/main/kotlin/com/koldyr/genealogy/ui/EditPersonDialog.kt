package com.koldyr.genealogy.ui

import java.awt.BorderLayout.SOUTH
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.FlowLayout.*
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagConstraints.NONE
import java.awt.GridBagConstraints.NORTHWEST
import java.awt.GridBagConstraints.VERTICAL
import java.awt.GridBagConstraints.WEST
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.*
import javax.swing.AbstractAction
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane.*
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.KeyStroke.*
import javax.swing.border.EmptyBorder
import org.apache.commons.lang3.StringUtils.*
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.renderer.FamilyRenderer
import com.koldyr.genealogy.renderer.LifeEventRenderer
import com.koldyr.genealogy.renderer.SpouseRenderer

/**
 * Description of class EditPersonDialog
 * @created: 2019-10-27
 */
class EditPersonDialog : JDialog {
    private val lineage: Lineage
    private var person: Person
    private var modalResult: Boolean = false

    private val txtName: JTextField
    private val txtMiddle: JTextField
    private val txtLast: JTextField
    private val txtMaiden: JTextField
    private val cmbGender: JComboBox<Gender>
    private val eventsModel: LifeEventListModel
    private val lstEvents: JList<LifeEvent>
    private val txtPlace: JTextField
    private val txtOccupation: JTextField
    private val txtNote: JTextArea
    private val cmbParentFamily: JComboBox<Family>
    private val cmbFamily: JComboBox<Person>

    constructor(owner: Frame?, lineage: Lineage, person: Person) : super(owner, "Edit Person", true) {
        this.lineage = lineage
        this.person = person

        val lblId = JLabel("Id:")
        val txtId = JTextField(person.id.toString())
        txtId.isEditable = false

        val name = person.name ?: PersonNames("", "", "", "")
        val lblName = JLabel("Name/Middle:")
        txtName = JTextField(name.first)
        txtName.minimumSize = Dimension(200, 20)
        txtMiddle = JTextField(name.middle)
        txtMiddle.minimumSize = Dimension(200, 20)

        val lblLast = JLabel("Last/Maiden:")
        txtLast = JTextField(name.last)
        txtMaiden = JTextField(name.maiden)

        val pnlNames2 = JPanel(GridBagLayout())
        val lblSex = JLabel("Gender:")
        cmbGender = JComboBox(Gender.values())
        cmbGender.addActionListener {
            txtMaiden.isVisible = cmbGender.selectedItem == Gender.FEMALE
            pnlNames2.revalidate()
        }

        val lblEvents = JLabel("Events:")
        eventsModel = LifeEventListModel(person.events.toMutableList())
        lstEvents = JList(eventsModel)
        lstEvents.cellRenderer = LifeEventRenderer()
        lstEvents.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == BUTTON1 && e.clickCount == 2) {
                    editEvent(lstEvents.selectedValue as LifeEvent)
                }
            }
        })

        val pnlEventButtons = createEventButtons()

        val lblPlace = JLabel("Place:")
        txtPlace = JTextField(person.place)

        val lblOccupation = JLabel("Occupation:")
        txtOccupation = JTextField(person.occupation)

        val lblNote = JLabel("Note:")
        txtNote = JTextArea(person.note, 4, 20)

        val lblParentFamily = JLabel("Parent Family:")
        cmbParentFamily = JComboBox(lineage.families.toTypedArray())
        cmbParentFamily.renderer = FamilyRenderer(lineage)
        cmbParentFamily.selectedItem = lineage.findFamily(person.parentFamilyId)

        val persons = lineage.persons
            .filter { if (person.gender == Gender.MALE) it.gender == Gender.FEMALE else it.gender == Gender.MALE }
            .toTypedArray()

        val lblFamily = JLabel("Married to:")
        cmbFamily = JComboBox(persons)
        cmbFamily.renderer = SpouseRenderer()
        cmbFamily.selectedItem = lineage.findFamily(person.familyId)?.let {
            if (person.gender == Gender.MALE) it.wife else it.husband
        }

        rootPane.border = EmptyBorder(10, 10, 10, 10)

        val zeroInsets = Insets(0, 0, 0, 0)
        val bottomRightInsets = Insets(0, 0, 5, 5)
        val bottomInsets = Insets(0, 0, 5, 0)

        val pnlNames1 = JPanel(GridBagLayout())
        pnlNames1.add(txtName, GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
            WEST, HORIZONTAL, zeroInsets, 0, 0))
        pnlNames1.add(txtMiddle, GridBagConstraints(1, 0, 1, 1, 0.5, 0.0,
            WEST, HORIZONTAL, Insets(0, 5, 0, 0), 0, 0))

        pnlNames2.add(txtLast, GridBagConstraints(0, 0, 1, 1, 0.5, 0.0,
            WEST, HORIZONTAL, zeroInsets, 0, 0))
        pnlNames2.add(txtMaiden, GridBagConstraints(1, 0, 1, 1, 0.5, 0.0,
            WEST, HORIZONTAL, Insets(0, 5, 0, 0), 0, 0))

        var rowIndex = 0
        val pnlContent = JPanel(GridBagLayout())
        pnlContent.add(lblId, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))

        pnlContent.add(txtId, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblName, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(pnlNames1, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblLast, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(pnlNames2, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblSex, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(cmbGender, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, NONE, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblEvents, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            NORTHWEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(JScrollPane(lstEvents), GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0,
            WEST, GridBagConstraints.BOTH, bottomInsets, 0, 0))
        pnlContent.add(pnlEventButtons, GridBagConstraints(2, rowIndex, 1, 1, 0.0, 0.0,
            NORTHWEST, VERTICAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblPlace, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(txtPlace, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblOccupation, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(txtOccupation, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblNote, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            NORTHWEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(JScrollPane(txtNote), GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblParentFamily, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(cmbParentFamily, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        rowIndex++
        pnlContent.add(lblFamily, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
            WEST, NONE, bottomRightInsets, 0, 0))
        pnlContent.add(cmbFamily, GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
            WEST, HORIZONTAL, bottomInsets, 0, 0))

        cmbGender.selectedItem = person.gender
        val pnlButtons = createButtonsPanel()

        contentPane.add(pnlContent)
        contentPane.add(pnlButtons, SOUTH)

        pack()
        setLocationRelativeTo(null)
    }

    fun getPerson(): Person {
        val name = txtName.text
        val middle = defaultIfEmpty(txtMiddle.text, null)
        val last = defaultIfEmpty(txtLast.text, null)
        val maiden = defaultIfEmpty(txtMaiden.text, null)
        person.name = PersonNames(name, middle, last, maiden)
        person.events = eventsModel.events.map { it.toPersonEvent() }.toMutableSet()
        person.gender = cmbGender.selectedItem as Gender
        person.place = defaultIfEmpty(txtPlace.text, null)
        person.occupation = defaultIfEmpty(txtOccupation.text, null)
        person.note = defaultIfEmpty(txtNote.text, null)

        return person
    }

    fun getModalResult(): Boolean {
        return modalResult
    }

    private fun createEventButtons(): JPanel {
        val btnAdd = JButton(object : AbstractAction("+") {
            override fun actionPerformed(e: ActionEvent) = editEvent(null)
        })
        btnAdd.margin = Insets(2, 3, 2, 3)

        val btnRemove = JButton(object : AbstractAction("-") {
            override fun actionPerformed(e: ActionEvent) = removeEvent()
        })
        btnRemove.margin = Insets(2, 5, 2, 4)

        val pnlEventButtons = JPanel()
        val boxLayout = BoxLayout(pnlEventButtons, BoxLayout.Y_AXIS)
        pnlEventButtons.layout = boxLayout

        pnlEventButtons.add(btnAdd)
        pnlEventButtons.add(btnRemove)
        return pnlEventButtons
    }

    private fun createButtonsPanel(): JPanel {
        val okAction = object : AbstractAction("Ok") {
            init {
                putValue(MNEMONIC_KEY, VK_ENTER)
            }

            override fun actionPerformed(evt: ActionEvent) = close(true)
        }

        val btnOk = JButton(okAction)
        btnOk.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(VK_ENTER, 0), "ok")
        btnOk.actionMap.put("ok", okAction)

        val cancelAction = object : AbstractAction("Cancel") {
            init {
                putValue(MNEMONIC_KEY, VK_ESCAPE)
            }

            override fun actionPerformed(evt: ActionEvent) = close(false)
        }

        val btnCancel = JButton(cancelAction)
        btnCancel.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(getKeyStroke(VK_ESCAPE, 0), "cancel")
        btnCancel.actionMap.put("cancel", cancelAction)

        val pnlButtons = JPanel(FlowLayout(TRAILING))
        pnlButtons.add(btnOk)
        pnlButtons.add(btnCancel)
        rootPane.defaultButton = btnOk

        return pnlButtons
    }

    private fun close(value: Boolean) {
        modalResult = value
        isVisible = false
    }

    private fun editEvent(event: LifeEvent?) {
        val eventEditPanel = LifeEventEditPanel(event)
        val result = showConfirmDialog(this, eventEditPanel, "Edit event", OK_CANCEL_OPTION)

        if (result == OK_OPTION) {
            val newEvent = eventEditPanel.getEvent()

            if (event == null) {
                eventsModel.add(newEvent)
            }
        }
    }

    private fun removeEvent() {
        lstEvents.selectedValue?.let {
            eventsModel.remove(lstEvents.selectedValue)
        }
    }
}
