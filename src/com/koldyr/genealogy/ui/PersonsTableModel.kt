package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import javax.swing.table.AbstractTableModel

/**
 * Description of class PersonsTableModel
 * @created: 2019-10-27
 */
class PersonsTableModel(private var persons: List<Person>): AbstractTableModel() {

    private val columnNames = listOf("Id", "Name", "Sex", "Birth", "Death", "Place", "Occupation", "Note", "Family Id")

    fun setPersons(persons: Collection<Person>) {
        this.persons = persons.toList().sortedBy { it.id }
        fireTableDataChanged()
    }

    fun getPerson(index: Int): Person {
        return persons[index]
    }

    override fun getRowCount(): Int {
        return persons.size
    }

    override fun getColumnCount(): Int {
        return columnNames.size
    }

    override fun getColumnName(column: Int): String {
        return columnNames[column]
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        return when (columnIndex) {
            1 -> { PersonNames::class.java }
            3,4 -> { LifeEvent::class.java }
            else -> { super.getColumnClass(columnIndex) }
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        val person = persons.get(rowIndex)
        return when (columnIndex) {
            0 -> { person.id }
            1 -> { person.name }
            2 -> { person.sex }
            3 -> { person.birth }
            4 -> { person.death }
            5 -> { person.place }
            6 -> { person.occupation }
            7 -> { person.note }
            8 -> { person.familyId }
            else -> { "N/A" }
        }
    }
}
