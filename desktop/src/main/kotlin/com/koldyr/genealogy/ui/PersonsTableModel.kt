package com.koldyr.genealogy.ui

import java.util.function.Predicate
import javax.swing.table.AbstractTableModel
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonNames

/**
 * Description of class PersonsTableModel
 * @created: 2019-10-27
 */
class PersonsTableModel : AbstractTableModel() {

    private var original: MutableList<Person>? = null
    private var persons = mutableListOf<Person>()
    private val columnNames = listOf("Id", "Name", "Gender", "Birth", "Death", "Place", "Occupation", "Note", "Family Id")

    fun setPersons(value: Collection<Person>) {
        persons = value.toMutableList()
        persons.sortBy(Person::id)
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
        fireTableRowsInserted(persons.size - 1, persons.size - 1)
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
        val person = persons[rowIndex]
        return when (columnIndex) {
            0 -> person.id
            1 -> person.name
            2 -> person.gender
            3 -> person.findEvent(EventType.Birth)
            4 -> person.findEvent(EventType.Death)
            5 -> person.place
            6 -> person.occupation
            7 -> person.note
            8 -> person.parentFamilyId
            else -> "N/A"
        }
    }

    fun filter(data: SearchData?) {
        if (data == null) {
            if (original != null) {
                persons = original!!
                fireTableDataChanged()
            }
        } else {
            if (original == null) {
                original = persons
            }

            val input = if (data.matchCase) data.input else data.input.lowercase()

            val checkFn: Predicate<String?> = if (data.wholeWord)
                Predicate {
                    if (it == null) {
                        return@Predicate false
                    }
                    if (data.matchCase) {
                        it.contains(input)
                    } else {
                        it.lowercase().contains(input)
                    }
                }
            else
                Predicate {
                    if (it == null) {
                        return@Predicate false
                    }
                    if (data.matchCase) {
                        it.equals(input)
                    } else {
                        it.lowercase().equals(input)
                    }
                }

            persons = original!!.filter { it.search(checkFn) }.toMutableList()
            persons.sortBy(Person::id)

            fireTableDataChanged()
        }
    }
}
