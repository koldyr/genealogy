package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.parser.FamilyTreeDataParser
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.JOptionPane.INFORMATION_MESSAGE
import javax.swing.JOptionPane.showMessageDialog
import javax.swing.filechooser.FileNameExtensionFilter

/**
 * Description of class GenealogyApp
 * @created: 2019-10-27
 */
class GenealogyApp : JFrame {

    private var persons: Collection<Person>
    private val tableModel: PersonsTableModel
    private val tblPersons: JTable
    private var fileName: String?

    constructor(persons: Collection<Person>, fileName: String?) : super("Genealogy: ${fileName ?: ""} ") {
        this.persons = persons
        this.fileName = fileName

//        var persons: Set<Person?> = families.stream()
//                .map { family: Family -> Stream.concat(Stream.of(family.husband, family.wife), family.children.stream()) }
//                .flatMap { stream: Stream<Person?> -> stream }
//                .filter { it != null }
//                .collect(Collectors.toSet())
//
//        persons = persons.toSortedSet(Comparator { p1, p2 -> p1!!.id.compareTo(p2!!.id) })

        tableModel = PersonsTableModel(persons.toList().sortedBy { it.id  })
        tblPersons = JTable(tableModel)
        tblPersons.setDefaultRenderer(PersonNames::class.java, NamesRenderer())
        tblPersons.setDefaultRenderer(LifeEvent::class.java, EventsRenderer())
        tblPersons.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && e.clickCount == 2) {
                    val person = tableModel.getPerson(tblPersons.selectedRow)
                    val editPersonDialog: JDialog = EditPersonDialog(this@GenealogyApp, person)
                    editPersonDialog.isVisible = true
                }
            }
        })

        contentPane.add(JScrollPane(tblPersons))

        createMenu()

        val frameSize = Dimension(1000, 800)
        preferredSize.size = frameSize
        size = frameSize

        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }

    private fun createMenu() {
        val actionOpen = object: AbstractAction("Open") {
            override fun actionPerformed(e: ActionEvent) {
                openFile()
            }
        }

        val actionSave = object: AbstractAction("Save") {
            override fun actionPerformed(e: ActionEvent) {
                saveFile()
            }
        }

        val actionExit = object: AbstractAction("Exit") {
            override fun actionPerformed(e: ActionEvent) {
                this@GenealogyApp.dispose()
            }
        }

        val actionAbout = object: AbstractAction("About") {
            override fun actionPerformed(e: ActionEvent) {
                showMessageDialog(this@GenealogyApp, "Genealogy v1.0.0", "About", INFORMATION_MESSAGE)
            }
        }

        val mnuFile = JMenu("File")
        mnuFile.add(actionOpen)
        mnuFile.add(actionSave)
        mnuFile.add(actionExit)

        val mnuHelp = JMenu("Help")
        mnuHelp.add(actionAbout)

        rootPane.jMenuBar = JMenuBar()
        rootPane.jMenuBar.add(mnuFile)
        rootPane.jMenuBar.add(mnuHelp)
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
}
