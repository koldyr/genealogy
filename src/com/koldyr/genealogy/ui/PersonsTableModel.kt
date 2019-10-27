package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import javax.swing.table.AbstractTableModel

/**
 * Description of class PersonsTableModel
 * @created: 2019-10-27
 */
class PersonsTableModel(private val persons: List<Person>): AbstractTableModel() {

    private val columnNames = listOf("Id", "Name", "Sex", "Birth", "Death", "Place", "Occupation", "Note", "Family Id")

    override fun getRowCount(): Int {
        return persons.size
    }

    override fun getColumnCount(): Int {
        return 9
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
            1 -> { if (person.name == null) "-" else person.name }
            2 -> { if (person.sex == null) "-" else person.sex!!.name }
            3 -> { if (person.birth == null) "-" else person.birth }
            4 -> { if (person.death == null) "-" else person.death }
            5 -> { if (person.place == null) "-" else person.place }
            6 -> { if (person.occupation == null) "-" else person.occupation }
            7 -> { if (person.note == null) "-" else person.note }
            8 -> { if (person.familyId == null) "-" else person.familyId }
            else -> { "N/A" }
        }
    }
}
