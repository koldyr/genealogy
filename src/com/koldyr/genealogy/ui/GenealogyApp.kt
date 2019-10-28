package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable

/**
 * Description of class GenealogyApp
 * @created: 2019-10-27
 */
class GenealogyApp : JFrame {

    private val persons: Collection<Person>
    private val tblPersons: JTable

    constructor(persons: Collection<Person>) : super("Genealogy") {
        this.persons = persons

//        var persons: Set<Person?> = families.stream()
//                .map { family: Family -> Stream.concat(Stream.of(family.husband, family.wife), family.children.stream()) }
//                .flatMap { stream: Stream<Person?> -> stream }
//                .filter { it != null }
//                .collect(Collectors.toSet())
//
//        persons = persons.toSortedSet(Comparator { p1, p2 -> p1!!.id.compareTo(p2!!.id) })

        val tableModel = PersonsTableModel(persons.toList().sortedBy { person: Person -> person.id  })
        tblPersons = JTable(tableModel)
        tblPersons.setDefaultRenderer(PersonNames::class.java, NamesRenderer())
        tblPersons.setDefaultRenderer(LifeEvent::class.java, EventsRenderer())
        tblPersons.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2) {
                    val person = tableModel.getPerson(tblPersons.selectedRow)
                    val editPersonDialog: JDialog = EditPersonDialog(this@GenealogyApp, person)
                    editPersonDialog.isVisible = true
                }
            }
        })

        contentPane.add(JScrollPane(tblPersons))

        val frameSize = Dimension(1000, 800)
        preferredSize.size = frameSize
        size = frameSize

        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }
}
