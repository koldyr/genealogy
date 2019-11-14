package com.koldyr.genealogy.ui

import com.koldyr.genealogy.export.ExporterFactory
import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import org.apache.commons.lang3.StringUtils.*
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
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Description of class GenealogyApp
 * @created: 2019-10-27
 */
class GenealogyApp : JFrame, ActionListener {

    private val personsModel: PersonsTableModel
    private val tblPersons: JTable

    private var lineage: Lineage
    private var file: File? = null

    constructor(lineage: Lineage, fileName: String?) : super("Genealogy: ${fileName ?: ""} ") {
        this.lineage = lineage
        this.file = fileName?.let { File(it) }

        createMenu()

        personsModel = PersonsTableModel()
        tblPersons = createTable()

        val pnlContent = contentPane as JPanel
        pnlContent.border = EmptyBorder(5, 5, 5, 5)
        pnlContent.add(JScrollPane(tblPersons))

        personsModel.setPersons(lineage.persons)

        val frameSize = Dimension(1000, 800)
        preferredSize.size = frameSize
        size = frameSize

        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    private fun createTable(): JTable {
        val tblPersons = JTable(personsModel, null)
        tblPersons.setDefaultRenderer(PersonNames::class.java, NamesRenderer())
        tblPersons.setDefaultRenderer(LifeEvent::class.java, EventsRenderer())
        tblPersons.addMouseListener(object : MouseAdapter() {

            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && e.clickCount == 2) {
                    editPerson()
                }
            }
        })
        return tblPersons
    }

    private fun createMenu() {
        val mnuFile = JMenu("File")

        val jmiOpen = JMenuItem("Open")
        jmiOpen.addActionListener(this)
        mnuFile.add(jmiOpen)

        val jmiSave = JMenuItem("Save")
        jmiSave.addActionListener(this)
        mnuFile.add(jmiSave)

        val jmiExport = JMenuItem("Export")
        jmiExport.addActionListener(this)
        mnuFile.add(jmiExport)

        val jmiExit = JMenuItem("Exit")
        jmiExit.addActionListener(this)
        mnuFile.add(jmiExit)

        val mnuEdit = JMenu("Edit")

        val jmiSearch = JMenuItem("Search")
        jmiSearch.addActionListener(this)
        mnuEdit.add(jmiSearch)

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
            "Export" -> exportFile()
            "Exit" -> dispose()
            "Edit" -> editPerson()
            "Delete" -> deletePerson()
            "Add" -> addPerson()
            "Search" -> searchPerson()
            "About" -> showAbout()
            else -> println(e.actionCommand)
        }
    }

    private fun searchPerson() {
        val searchPane = SearchPersonPanel()
        val result = JOptionPane.showConfirmDialog(this, searchPane, "Search", JOptionPane.OK_CANCEL_OPTION)

        if (result == JOptionPane.OK_OPTION) {
            val data = searchPane.getSearch()
            personsModel.filter(data)
        }
    }

    private fun editPerson() {
        if (tblPersons.selectedRow > -1) {
            val person = personsModel.getPerson(tblPersons.selectedRow)
            val toEdit = clonePerson(person)

            val editPersonDialog = EditPersonDialog(this, lineage, toEdit)
            editPersonDialog.isVisible = true

            if (editPersonDialog.getModalResult()) {
                val changed = editPersonDialog.getPerson()
                personsModel.update(changed)
            }
        }
    }

    private fun addPerson() {
        val index = personsModel.getAll().stream()
                .map(Person::id)
                .max(Integer::compare)
                .get() + 1
        var person = Person(index)

        val editPersonDialog = EditPersonDialog(this, lineage, person)
        editPersonDialog.isVisible = true

        if (editPersonDialog.getModalResult()) {
            person = editPersonDialog.getPerson()
            personsModel.add(person)
        }
    }

    private fun deletePerson() {
        if (tblPersons.selectedRow > -1) {
            personsModel.remove(tblPersons.selectedRow)
        }
    }

    private fun openFile() {
        val startDir = System.getProperty("user.dir")
        val fileChooser = JFileChooser(startDir)
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("CSV genealogy file", "csv"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("JSON genealogy file", "json"))
        fileChooser.showOpenDialog(this)

        if (fileChooser.selectedFile != null) {
            val fileToOpen: File = fileChooser.selectedFile

            val importer = ImporterFactory.create(fileToOpen)
            lineage = importer.import(fileToOpen.toPath())
            personsModel.setPersons(lineage.persons)

            title = "Genealogy: ${fileToOpen.absolutePath}"
            file = fileToOpen
        }
    }


    private fun saveFile() {
        if (file != null) {
            val fileToSave = file!!
            val extension = fileToSave.extension

            val exporter = ExporterFactory.create(extension)
            lineage.persons = personsModel.getAll()
            exporter.export(lineage, fileToSave.toPath())

            JOptionPane.showMessageDialog(this, "Saved to ${fileToSave.name}")
        }
    }

    private fun exportFile() {
        val startDir = System.getProperty("user.dir")
        val fileChooser = JFileChooser(startDir)
        fileChooser.isAcceptAllFileFilterUsed = false
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("JSON genealogy file", "json"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("CSV genealogy file", "csv"))
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.showSaveDialog(this)

        if (fileChooser.selectedFile != null) {
            if (fileChooser.selectedFile.exists()) {
                val replace = JOptionPane.showConfirmDialog(this, "Replace existing file", "File exists", JOptionPane.YES_NO_OPTION)
                if (replace == JOptionPane.NO_OPTION) {
                    return
                }
            }

            val persons = personsModel.getAll()
            val filter = fileChooser.fileFilter as FileNameExtensionFilter
            val extension = filter.extensions[0]
            val file = handleExportFile(fileChooser.selectedFile, extension)

            val exporter = ExporterFactory.create(extension)
            lineage.persons = persons
            exporter.export(lineage, file.toPath())

            JOptionPane.showMessageDialog(this, "Exported to ${file.name}")
        }
    }

    private fun handleExportFile(file: File, ext: String): File {
        val fileExt = file.extension
        if (isEmpty(fileExt)) {
            val fileName = file.name
            return File(file.parentFile, "$fileName.$ext")
        }

        return file
    }

    private fun showAbout() {
        JOptionPane.showMessageDialog(this, "Genealogy v1.0.0", "About", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun clonePerson(person: Person): Person {
        val copy = person.copy()
        copy.name = person.name?.copy()
        copy.events = person.events.map { it.copy() }.toMutableSet()
        return copy
    }
}
