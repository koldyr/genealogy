package com.koldyr.genealogy.persistence

import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Types.*
import java.time.LocalDate
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person

/**
 * Description of class ImportRepository
 *
 * @author d.halitski@gmail.com
 * @created: 2022-06-25
 */
class ImportRepository(private val jdbc: JdbcTemplate) {

    fun save(person: Person) {
        person.id = nextPersonId()
        person.familyId = null
        person.parentFamilyId = null

        val sql = "insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID, LINEAGE_ID) " +
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        jdbc.execute(sql) { statement ->
            statement.setLong(1, person.id!!)
            setString(statement, 2, person.place)
            setString(statement, 3, person.occupation)
            setString(statement, 4, person.note)
            setString(statement, 5, person.gender.name.substring(0, 1))
            setString(statement, 6, person.name?.first)
            setString(statement, 7, person.name?.middle)
            setString(statement, 8, person.name?.last)
            setString(statement, 9, person.name?.maiden)
            statement.setLong(10, person.user!!.id!!)
            statement.setLong(11, person.lineageId!!)
            statement.executeUpdate()
        }

        saveEvents(person)
    }

    fun save(family: Family) {
        family.id = nextFamilyId()
        family.husband?.let { it.familyId = family.id }
        family.wife?.let { it.familyId = family.id }
        family.children.forEach { it.parentFamilyId = family.id }

        val sql = "insert into T_FAMILY (FAMILY_ID, HUSBAND_ID, WIFE_ID, NOTE, USER_ID, LINEAGE_ID) values (?, ?, ?, ?, ?, ?)"
        jdbc.execute(sql) { statement ->
            statement.setLong(1, family.id!!)
            setLong(statement, 2, family.husband?.id)
            setLong(statement, 3, family.wife?.id)
            setString(statement, 4, family.note)
            statement.setLong(5, family.user.id!!)
            statement.setLong(6, family.lineageId!!)
            statement.executeUpdate()
        }

        family.husband?.let {
            setFamily(family.id!!, it.id!!)
        }
        family.wife?.let {
            setFamily(family.id!!, it.id!!)
        }
        family.children.forEach {
            setParentFamily(family.id!!, it.id!!)
        }

        saveChildren(family)
        saveEvents(family)
    }

    private fun saveChildren(family: Family) {
        if (family.children.isEmpty()) return

        val children = family.children.iterator()
        val sql = "insert into T_CHILDREN (FAMILY_ID, PERSON_ID) values (?, ?)"
        jdbc.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                statement.setLong(1, family.id!!)
                statement.setLong(2, children.next().id!!)
            }

            override fun getBatchSize(): Int = family.children.size
        })
    }

    private fun saveEvents(family: Family) {
        if (family.events.isEmpty()) return

        val events = family.events.iterator()
        val sql = "insert into T_FAMILY_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, FAMILY_ID) values (?, ?, ?, ?, ?, ?)"
        jdbc.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val event = events.next()
                statement.setLong(1, nextEventId())
                statement.setString(2, event.type.getCode())
                setString(statement, 3, event.place)
                setString(statement, 4, event.note)
                setDate(statement, 5, event.date)
                statement.setLong(6, family.id!!)
            }

            override fun getBatchSize(): Int = family.events.size
        })
    }

    private fun saveEvents(person: Person) {
        if (person.events.isEmpty()) return

        val events = person.events.iterator()
        val sql = "insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID) " +
            "values (?, ?, ?, ?, ?, ?)"
        jdbc.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val event = events.next()
                statement.setLong(1, nextEventId())
                statement.setString(2, event.type.getCode())
                setString(statement, 3, event.place)
                setString(statement, 4, event.note)
                setDate(statement, 5, event.date)
                statement.setLong(6, person.id!!)
            }

            override fun getBatchSize(): Int = person.events.size
        })
    }

    private fun setFamily(familyId: Long, personId: Long) {
        val sql = "update T_PERSON set FAMILY_ID = ? where PERSON_ID = ?"
        jdbc.execute(sql) { statement ->
            statement.setLong(1, familyId)
            statement.setLong(2, personId)
            statement.executeUpdate()
        }
    }

    private fun setParentFamily(familyId: Long, personId: Long) {
        val sql = "update T_PERSON set PARENT_FAMILY_ID = ? where PERSON_ID = ?"
        jdbc.execute(sql) { statement ->
            statement.setLong(1, familyId)
            statement.setLong(2, personId)
            statement.executeUpdate()
        }
    }

    private fun nextPersonId(): Long = nextId("select NEXTVAL('SEQ_PERSON')")

    private fun nextFamilyId(): Long = nextId("select NEXTVAL('SEQ_FAMILY')")

    private fun nextEventId(): Long = nextId("select NEXTVAL('SEQ_EVENT')")

    private fun nextId(sql: String): Long {
        return jdbc.query(sql, ResultSetExtractor {
            it.first()
            it.getLong(1)
        })!!
    }
    
    private fun setLong(statement: PreparedStatement, index: Int, value: Long?) {
        if (value == null) {
            statement.setNull(index, BIGINT)
        } else {
            statement.setLong(index, value)
        }
    }

    private fun setString(statement: PreparedStatement, index: Int, value: String?) {
        if (value == null) {
            statement.setNull(index, VARCHAR)
        } else {
            statement.setString(index, value)
        }
    }

    private fun setDate(statement: PreparedStatement, index: Int, value: LocalDate?) {
        if (value == null) {
            statement.setNull(index, DATE)
        } else {
            statement.setDate(index, Date.valueOf(value))
        }
    }
}