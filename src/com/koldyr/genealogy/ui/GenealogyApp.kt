package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.Persons
import com.koldyr.genealogy.parser.FamilyTreeDataParser
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.nio.file.Files
import java.time.format.DateTimeFormatter
import java.util.*
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
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller.*

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

            var toEdit = clonePerson(person)
            val editPersonDialog = EditPersonDialog(this@GenealogyApp, toEdit)
            editPersonDialog.isVisible = true

            if (editPersonDialog.getModalResult()) {
                toEdit = editPersonDialog.getPerson()
                tableModel.updatePerson(toEdit)
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
        val fileChooser = JFileChooser()
        fileChooser.addChoosableFileFilter(FileNameExtensionFilter("AgelongTree genealogy file", "ged"))
        fileChooser.showOpenDialog(this)

        if (fileChooser.selectedFile != null) {
            fileName = fileChooser.selectedFile.absolutePath
            val persons = FamilyTreeDataParser().parse(fileName!!)
            tableModel.setPersons(persons)

            title = "Genealogy: $fileName"
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
            val filter: FileNameExtensionFilter = fileChooser.fileFilter as FileNameExtensionFilter
            val extension: String = filter.extensions[0]
            when (extension) {
                "xml" -> saveXML(fileChooser.selectedFile)
                "json" -> saveJSON(fileChooser.selectedFile)
                "ged" -> saveGED(fileChooser.selectedFile)
                else -> saveCSV(fileChooser.selectedFile)
            }
            println("Completed")
        }
    }

    private fun saveXML(file: File) {
        try {
            val jaxbContext = JAXBContext.newInstance(Persons::class.java)
            val jaxbMarshaller = jaxbContext.createMarshaller()
            jaxbMarshaller.setProperty(JAXB_FORMATTED_OUTPUT, java.lang.Boolean.TRUE)

            val stream = Files.newOutputStream(file.toPath())
            stream.buffered().bufferedWriter(Charsets.UTF_8).use { writer ->
                val persons = tableModel.getPersons()
                jaxbMarshaller.marshal(Persons(persons), writer)
            }
        } catch (e: JAXBException) {
            e.printStackTrace()
        }
    }

    private fun saveJSON(file: File) {
        TODO("not implemented")
    }

    private fun saveGED(file: File) {
        TODO("not implemented")
    }

    private fun saveCSV(file: File) {
        try {
            val stream = Files.newOutputStream(file.toPath())
            stream.buffered().bufferedWriter(Charsets.UTF_8).use { writer ->
                tableModel.getPersons().forEach {
                    val line = StringJoiner(",", "", "\n")
                    line.add(it.id.toString())

                    if (it.name == null) {
                        line.add("")
                    } else {
                        line.add(it.name!!.name + '|' + it.name!!.middle + '|' + it.name!!.last + '|' + it.name!!.maiden)
                    }

                    if (it.birth == null) {
                        line.add("")
                    } else {
                        line.add(it.birth!!.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) + '|' + it.birth!!.place)
                    }
                    if (it.death == null) {
                        line.add("")
                    } else {
                        line.add(it.death!!.date?.format(DateTimeFormatter.ISO_LOCAL_DATE) + '|' + it.death!!.place)
                    }

                    line.add(it.sex.name)
                    line.add(it.place ?: "")
                    line.add(it.occupation ?: "")
                    line.add(it.note ?: "")
                    line.add(it.familyId?.toString() ?: "")

                    writer.write(line.toString())
                }

            }
        } catch (e: JAXBException) {
            e.printStackTrace()
        }
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
