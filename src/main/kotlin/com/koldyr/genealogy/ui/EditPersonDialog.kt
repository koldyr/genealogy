package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Sex
import org.apache.commons.lang3.StringUtils.*
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
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
    private val lineage: Lineage
    private var person: Person
    private var modalResult: Boolean = false

    private val txtName: JTextField
    private val txtMiddle: JTextField
    private val txtLast: JTextField
    private val txtMaiden: JTextField
    private val cmbSex: JComboBox<Sex>
    private val eventsModel: LifeEventListModel
    private val lstEvents: JList<LifeEvent>
    private val txtPlace: JTextField
    private val txtOccupation: JTextField
    private val txtNote: JTextArea
    private val cmbFamily: JComboBox<Family>

    constructor(owner: Frame?, lineage: Lineage, person: Person) : super(owner, "Edit Person", true) {
        this.lineage = lineage
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

        val lblEvents = JLabel("Events:")
        eventsModel = LifeEventListModel(person.events.toMutableList())
        lstEvents = JList(eventsModel)
        lstEvents.cellRenderer = LifeEventRenderer()
        lstEvents.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && e.clickCount == 2) {
                    editEvent(lstEvents.selectedValue as LifeEvent)
                }
            }
        })

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

        val lblPlace = JLabel("Place:")
        txtPlace = JTextField(person.place)

        val lblOccupation = JLabel("Occupation:")
        txtOccupation = JTextField(person.occupation)

        val lblNote = JLabel("Note:")
        txtNote = JTextArea(person.note, 4, 20)

        val lblFamily = JLabel("Family:")
        cmbFamily = JComboBox(lineage.families.toTypedArray())
        cmbFamily.renderer = FamilyRenderer(lineage)
        cmbFamily.selectedItem = lineage.findFamily(person.familyId)

        rootPane.border = EmptyBorder(10, 10, 10, 10)

        var rowIndex = 0
        val pnlContent = JPanel(GridBagLayout())
        pnlContent.add(lblId, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtId, GridBagConstraints(1, rowIndex, 3, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblName, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtName, GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtMiddle, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblLast, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtLast, GridBagConstraints(1, rowIndex, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(txtMaiden, GridBagConstraints(2, rowIndex, 1, 1, 0.5, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblSex, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(cmbSex, GridBagConstraints(1, rowIndex, 3, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblEvents, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(JScrollPane(lstEvents), GridBagConstraints(1, rowIndex, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, Insets(0, 0, 5, 0), 0, 0))
        pnlContent.add(pnlEventButtons, GridBagConstraints(3, rowIndex, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, Insets(0, 5, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblPlace, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtPlace, GridBagConstraints(1, rowIndex, 3, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblOccupation, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(txtOccupation, GridBagConstraints(1, rowIndex, 3, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblNote, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(JScrollPane(txtNote), GridBagConstraints(1, rowIndex, 3, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, Insets(0, 0, 5, 0), 0, 0))

        rowIndex++
        pnlContent.add(lblFamily, GridBagConstraints(0, rowIndex, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, Insets(0, 0, 5, 5), 0, 0))
        pnlContent.add(cmbFamily, GridBagConstraints(1, rowIndex, 3, 1, 1.0, 0.0,
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
        person.events = eventsModel.events.toMutableSet()
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

    private fun editEvent(event: LifeEvent?) {
        val eventEditPanel = LifeEventEditPanel(event)
        val result = JOptionPane.showConfirmDialog(this, eventEditPanel, "Edit event", JOptionPane.OK_CANCEL_OPTION)

        if (result == JOptionPane.OK_OPTION) {
            val newEvent = eventEditPanel.getEvent()

            if (event == null) {
                eventsModel.add(newEvent)
            }
        }
    }

    private fun removeEvent() {
        if (lstEvents.selectedValue != null) {
            eventsModel.remove(lstEvents.selectedValue)
        }
    }
}
