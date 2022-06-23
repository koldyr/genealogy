package com.koldyr.genealogy

import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseEvent.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.function.ToLongFunction
import javax.swing.AbstractAction
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane.*
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.TableRowSorter
import org.apache.commons.lang3.StringUtils.*
import com.koldyr.genealogy.export.ExporterFactory
import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.renderer.EventsRenderer
import com.koldyr.genealogy.ui.EditPersonDialog
import com.koldyr.genealogy.ui.NamesRenderer
import com.koldyr.genealogy.ui.PersonsTableModel
import com.koldyr.genealogy.ui.SearchPersonPanel

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
                if (e.button == BUTTON1 && e.clickCount == 2) {
                    editPerson()
                }
            }
        })

        tblPersons.autoCreateRowSorter = true

        val eventComparator = Comparator(LifeEvent::compareTo)
        val intComparator = Comparator(Integer::compare)

        val rowSorter = tblPersons.rowSorter as TableRowSorter
        rowSorter.setComparator(0, intComparator)
        rowSorter.setComparator(3, eventComparator)
        rowSorter.setComparator(4, eventComparator)
        rowSorter.setComparator(8, intComparator)
        return tblPersons
    }

    private fun createMenu() {
        val mnuFile = JMenu("File")
        mnuFile.add(JMenuItem(MenuItemAction("Open")))
        mnuFile.add(JMenuItem(MenuItemAction("Save")))
        mnuFile.add(JMenuItem(MenuItemAction("Export")))
        mnuFile.add(JMenuItem(MenuItemAction("Exit")))

        val mnuEdit = JMenu("Edit")
        mnuEdit.add(JMenuItem(MenuItemAction("Search")))
        mnuEdit.add(JMenuItem(MenuItemAction("Add")))
        mnuEdit.add(JMenuItem(MenuItemAction("Edit")))
        mnuEdit.add(JMenuItem(MenuItemAction("Delete")))

        val mnuHelp = JMenu("Help")
        mnuHelp.add(JMenuItem(MenuItemAction("About")))

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
        val result = showConfirmDialog(this, searchPane, "Search", OK_CANCEL_OPTION)

        if (result == OK_OPTION) {
            val data = searchPane.getSearch()
            personsModel.filter(data)
        }
    }

    private fun editPerson() {
        if (tblPersons.selectedRow > -1) {
            val index = tblPersons.rowSorter.convertRowIndexToModel(tblPersons.selectedRow)
            val person = personsModel.getPerson(index)
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
        val index = personsModel.getAll()
            .stream()
            .map(Person::id)
            .filter(Objects::nonNull)
            .mapToLong(ToLongFunction<Long?> { it })
            .max()
            .orElse(0) + 1

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
            val index = tblPersons.rowSorter.convertRowIndexToModel(tblPersons.selectedRow)
            personsModel.remove(index)
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

        fileChooser.selectedFile?.let {
            val importer = ImporterFactory.create(it)
            lineage = importer.import(it.toPath())
            personsModel.setPersons(lineage.persons)

            title = "Genealogy: ${it.absolutePath}"
            file = it
        }
    }

    private fun saveFile() {
        file?.let {
            val extension = it.extension

            val exporter = ExporterFactory.create(extension)
            lineage.persons = personsModel.getAll()
            exporter.export(lineage, it.toPath())

            showMessageDialog(this, "Saved to ${it.name}")
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

        fileChooser.selectedFile?.let {
            if (it.exists()) {
                val replace = showConfirmDialog(this, "Replace existing file", "File exists", YES_NO_OPTION)
                if (replace == NO_OPTION) {
                    return
                }
            }

            val persons = personsModel.getAll()
            val filter = fileChooser.fileFilter as FileNameExtensionFilter
            val extension = filter.extensions[0]
            val file = handleExportFile(it, extension)

            val exporter = ExporterFactory.create(extension)
            lineage.persons = persons
            exporter.export(lineage, file.toPath())

            showMessageDialog(this, "Exported to ${file.name}")
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
        showMessageDialog(this, "Genealogy v2.0.0", "About", INFORMATION_MESSAGE)
    }

    private fun clonePerson(person: Person): Person {
        val copy = person.clone()
        copy.name = person.name?.clone()
        copy.events = person.events.map(PersonEvent::clone).toMutableSet()
        return copy
    }

    private inner class MenuItemAction(name: String) : AbstractAction(name) {

        constructor(name: String, action: String) : this(name) {
            putValue(ACTION_COMMAND_KEY, action)
        }

        init {
            putValue(ACTION_COMMAND_KEY, name)
        }

        override fun actionPerformed(e: ActionEvent) {
            this@GenealogyApp.actionPerformed(e)
        }
    }
}

@Throws(IOException::class)
fun main(args: Array<String>) {
    val lineage: Lineage
    val fileName = if (args.isEmpty()) null else args[0]
    if (fileName == null) {
        lineage = Lineage(listOf(), setOf())
    } else {
        val file = File(fileName)
        val parser = ImporterFactory.create(file)
        lineage = parser.import(file.toPath())
        println("lineage = $lineage")
    }

    val appWindow = GenealogyApp(lineage, fileName)
    appWindow.isVisible = true
}