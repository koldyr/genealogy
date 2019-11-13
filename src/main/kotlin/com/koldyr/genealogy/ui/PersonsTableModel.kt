package com.koldyr.genealogy.ui

import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames
import javax.swing.table.AbstractTableModel

/**
 * Description of class PersonsTableModel
 * @created: 2019-10-27
 */
class PersonsTableModel : AbstractTableModel() {

    private var persons: MutableList<Person> = mutableListOf()
    private val columnNames = listOf("Id", "Name", "Gender", "Birth", "Death", "Place", "Occupation", "Note", "Family Id")

    fun setPersons(value: Collection<Person>) {
        persons = value as? MutableList ?: value.toMutableList()
        persons.sortBy { it.id }
        fireTableDataChanged()
    }

    fun getAll(): Collection<Person> {
        return persons
    }

    fun getPerson(index: Int): Person {
        return persons[index]
    }

    fun remove(index: Int): Person {
        val person = persons.removeAt(index)
        fireTableRowsDeleted(index, index)
        return person
    }

    fun add(person: Person) {
        persons.add(person)
        fireTableRowsInserted(persons.size, persons.size)
    }

    fun update(person: Person) {
        for ((index, p) in persons.withIndex()) {
            if (p.id == person.id) {
                persons.removeAt(index)
                persons.add(index, person)
                fireTableRowsUpdated(index, index)
                return
            }
        }
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
            1 -> PersonNames::class.java
            3, 4 -> LifeEvent::class.java
            else -> super.getColumnClass(columnIndex)
        }
    }

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
        val person = persons.get(rowIndex)
        return when (columnIndex) {
            0 -> person.id
            1 -> person.name
            2 -> person.gender
            3 -> person.getBirth()
            4 -> person.getDeath()
            5 -> person.place
            6 -> person.occupation
            7 -> person.note
            8 -> person.familyId
            else -> "N/A"
        }
    }
}
