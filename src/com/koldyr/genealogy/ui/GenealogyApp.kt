package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.parser.FamilyTreeDataParser
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Description of class GenealogyApp
 * @created: 2019-10-27
 */
class GenealogyApp : JFrame, ActionListener {

    private val tableModel: PersonsTableModel
    private val tblPersons: JTable

    private var persons: Collection<Person>
    private var fileName: String?

    constructor(persons: Collection<Person>, fileName: String?) : super("Genealogy: ${fileName ?: ""} ") {
        this.persons = persons
        this.fileName = fileName

//        var persons: Set<Person?> = families.stream()
//                .map { family: Family -> Stream.concat(Stream.of(family.husband, family.wife), family.children.stream()) }
//                .flatMap { stream: Stream<Person?> -> stream }
//                .filter { it != null }
//                .collect(Collectors.toSet())

        val personPopUp = createPersonPopUp()
        createMenu()

        tableModel = PersonsTableModel()
        tblPersons = createTable(personPopUp)
        contentPane.add(JScrollPane(tblPersons))

        tableModel.setPersons(persons)

        val frameSize = Dimension(1000, 800)
        preferredSize.size = frameSize
        size = frameSize

        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    private fun createTable(personPopUp: JPopupMenu): JTable {
        val tblPersons = JTable(tableModel, null)
        tblPersons.setDefaultRenderer(PersonNames::class.java, NamesRenderer())
        tblPersons.setDefaultRenderer(LifeEvent::class.java, EventsRenderer())
        tblPersons.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    personPopUp.location = e.locationOnScreen
                    personPopUp.isVisible = true
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger) {
                    personPopUp.location = e.locationOnScreen
                    personPopUp.isVisible = true
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && e.clickCount == 2) {
                    editPerson()
                }
            }
        })
        return tblPersons
    }

    private fun createPersonPopUp(): JPopupMenu {
        val personPopUp = JPopupMenu()

        val jmiAdd = JMenuItem("Add")
        jmiAdd.addActionListener(this)
        personPopUp.add(jmiAdd)

        val jmiEdit = JMenuItem("Edit")
        jmiEdit.addActionListener(this)
        personPopUp.add(jmiEdit)

        val jmiDelete = JMenuItem("Delete")
        jmiDelete.addActionListener(this)
        personPopUp.add(jmiDelete)

        return personPopUp
    }

    private fun createMenu() {
        val mnuFile = JMenu("File")

        val jmiOpen = JMenuItem("Open")
        jmiOpen.addActionListener(this)
        mnuFile.add(jmiOpen)

        val jmiSave = JMenuItem("Save")
        jmiSave.addActionListener(this)
        mnuFile.add(jmiSave)

        val jmiExit = JMenuItem("Exit")
        jmiExit.addActionListener(this)
        mnuFile.add(jmiExit)

        val mnuEdit = JMenu("Edit")

        val jmiAdd = JMenuItem("Add")
        jmiAdd.addActionListener(this)
        mnuEdit.add(jmiAdd)

        val jmiEdit = JMenuItem("Edit")
        jmiEdit.addActionListener(this)
        mnuEdit.add(jmiEdit)

        val jmiDelete = JMenuItem("Delete")
        jmiDelete.addActionListener(this)
        mnuEdit.add(jmiDelete)

        val mnuHelp = JMenu("Help")

        val jmiAbout = JMenuItem("About")
        jmiAbout.addActionListener(this)
        mnuHelp.add(jmiAbout)

        rootPane.jMenuBar = JMenuBar()
        rootPane.jMenuBar.add(mnuFile)
        rootPane.jMenuBar.add(mnuEdit)
        rootPane.jMenuBar.add(mnuHelp)
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.actionCommand) {
            "Open" -> openFile()
            "Save" -> saveFile()
            "Exit" -> dispose()
            "Edit" -> editPerson()
            "Delete" -> deletePerson()
            "Add" -> addPerson()
            "About" -> showAbout()
            else -> println(e.actionCommand)
        }
    }

    private fun editPerson() {
        if (tblPersons.selectedRow > -1) {
            val person = tableModel.getPerson(tblPersons.selectedRow)

            var toEdit = person.copy()
            val editPersonDialog = EditPersonDialog(this@GenealogyApp, toEdit)
            editPersonDialog.isVisible = true

            if (editPersonDialog.getModalResult()) {
                toEdit = editPersonDialog.getPerson()
                tableModel.updatePerson(toEdit)
            }
        }
    }

    private fun addPerson() {
        val index = persons.stream()
                .map { it.id }
                .max { id1, id2 -> id1.compareTo(id2) }
                .get() + 1
        var person = Person(index)

        val editPersonDialog = EditPersonDialog(this@GenealogyApp, person)
        editPersonDialog.isVisible = true

        if (editPersonDialog.getModalResult()) {
            person = editPersonDialog.getPerson()
            tableModel.addPerson(person)
        }
    }

    private fun deletePerson() {
        if (tblPersons.selectedRow > -1) {
            tableModel.removePerson(tblPersons.selectedRow)
        }
    }

    private fun openFile() {
        val fileChooser = JFileChooser()
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.showOpenDialog(this)

        if (fileChooser.selectedFile != null) {
            fileName = fileChooser.selectedFile.absolutePath
            persons = FamilyTreeDataParser().parse(fileName!!)
            tableModel.setPersons(persons)

            title = "Genealogy: $fileName"
        }
    }

    private fun saveFile() {
        val fileChooser = JFileChooser()
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("XML genealogy file", "xml"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("CSV genealogy file", "csv"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("JSON genealogy file", "json"))
        fileChooser.showSaveDialog(this)

        if (fileChooser.selectedFile != null) {
            println("path: ${fileChooser.selectedFile}")
        }
    }

    private fun showAbout() {
        JOptionPane.showMessageDialog(this@GenealogyApp, "Genealogy v1.0.0", "About", JOptionPane.INFORMATION_MESSAGE)
    }
}
