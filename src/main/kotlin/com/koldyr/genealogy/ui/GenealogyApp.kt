package com.koldyr.genealogy.ui

import com.koldyr.genealogy.export.ExporterFactory
import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Description of class GenealogyApp
 * @created: 2019-10-27
 */
class GenealogyApp : JFrame, ActionListener {

    private val tableModel: PersonsTableModel
    private val tblPersons: JTable

    private var fileName: String?

    constructor(persons: Collection<Person>, fileName: String?) : super("Genealogy: ${fileName ?: ""} ") {
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

        val pnlContent = contentPane as JPanel
        pnlContent.border = EmptyBorder(5, 5, 5, 5)
        pnlContent.add(JScrollPane(tblPersons))

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
            val toEdit = clonePerson(person)

            val editPersonDialog = EditPersonDialog(this@GenealogyApp, toEdit)
            editPersonDialog.isVisible = true

            if (editPersonDialog.getModalResult()) {
                val changed = editPersonDialog.getPerson()
                tableModel.updatePerson(changed)
            }
        }
    }

    private fun addPerson() {
        val index = tableModel.getPersons().stream()
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
        val startDir = System.getProperty("user.dir")
        val fileChooser = JFileChooser(startDir)
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("XML genealogy file", "xml"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("CSV genealogy file", "csv"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("JSON genealogy file", "json"))
        fileChooser.showOpenDialog(this)

        if (fileChooser.selectedFile != null) {
            val file = fileChooser.selectedFile

            val importer = ImporterFactory.create(file)
            val persons = importer.import(file)
            tableModel.setPersons(persons)

            title = "Genealogy: ${file.absolutePath}"
        }
    }

    private fun saveFile() {
        val startDir = System.getProperty("user.dir")
        val fileChooser = JFileChooser(startDir)
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("XML genealogy file", "xml"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("CSV genealogy file", "csv"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("JSON genealogy file", "json"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.showSaveDialog(this)

        if (fileChooser.selectedFile != null) {
            val persons = tableModel.getPersons()
            val filter = fileChooser.fileFilter as FileNameExtensionFilter
            val extension = filter.extensions[0]
            val file = handleExportFile(fileChooser.selectedFile, extension)

            val exporter = ExporterFactory.create(extension)
            exporter.export(file, persons)
        }
    }

    private fun handleExportFile(file: File, ext: String): File {
        val fileName = file.name
        val index = fileName.indexOf('.')
        if (index < 0) {
            return File(file.parentFile, "$fileName.$ext")
        }

        return file
    }

    private fun showAbout() {
        JOptionPane.showMessageDialog(this@GenealogyApp, "Genealogy v1.0.0", "About", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun clonePerson(person: Person): Person {
        val copy = person.copy()
        copy.name = person.name?.copy()
        copy.birth = person.birth?.copy()
        copy.death = person.death?.copy()
        return copy
    }
}
