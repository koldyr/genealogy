package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import java.awt.Dimension
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.TableModel

/**
 * Description of class GenealogyApp
 * @created: 2019-10-27
 */
class GenealogyApp : JFrame {

    private val families: Set<Family>
    private val tblPersons: JTable

    constructor(families: Set<Family>) : super("Genealogy") {
        this.families = families

        var persons: Set<Person?> = families.stream()
                .map { family: Family -> Stream.concat(Stream.of(family.husband, family.wife), family.children.stream()) }
                .flatMap { stream: Stream<Person?> -> stream }
                .filter { it != null }
                .collect(Collectors.toSet())

        persons = persons.toSortedSet(Comparator { p1, p2 -> p1!!.id.compareTo(p2!!.id) })

        val tableModel: TableModel = PersonsTableModel(persons.toList() as List<Person>)
        tblPersons = JTable(tableModel)
        tblPersons.setDefaultRenderer(PersonNames::class.java, NamesRenderer())
        contentPane.add(JScrollPane(tblPersons))

        val frameSize = Dimension(1000, 800)
        preferredSize.size = frameSize
        size = frameSize

        defaultCloseOperation = EXIT_ON_CLOSE
        setLocationRelativeTo(null)
    }
}
