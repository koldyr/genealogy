package com.koldyr.genealogy.persistence

import java.sql.Date
import java.sql.PreparedStatement
import java.sql.Types.*
import java.time.LocalDate
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.LifeEvent
import com.koldyr.genealogy.model.Person

/**
 * Description of class ImportRepositoryImpl
 *
 * @author d.halitski@gmail.com
 * @created: 2022-06-25
 */
class ImportRepositoryImpl(private val jdbc: JdbcTemplate) : ImportRepository {

    private val insertPerson = "insert into T_PERSON (PERSON_ID, PLACE, OCCUPATION, NOTE, GENDER, FIRST_NAME, MIDDLE_NAME, LAST_NAME, MAIDEN_NAME, USER_ID, LINEAGE_ID) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    private val insertPersonEvent = "insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID) values (?, ?, ?, ?, ?, ?)"

    private val insertFamily = "insert into T_FAMILY (FAMILY_ID, HUSBAND_ID, WIFE_ID, NOTE, USER_ID, LINEAGE_ID) values (?, ?, ?, ?, ?, ?)"
    private val insertFamilyEvent = "insert into T_FAMILY_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, FAMILY_ID) values (?, ?, ?, ?, ?, ?)"

    override fun save(person: Person) {
        preparePerson(person)

        jdbc.execute(insertPerson) { statement ->
            personToStatement(statement, person)
            statement.executeUpdate()
        }

        saveEvents(person)
    }

    override fun save(family: Family) {
        prepareFamily(family)

        jdbc.execute(insertFamily) { statement ->
            familyToStatement(statement, family)
            statement.executeUpdate()
        }

        setFamilyIds(family)
        saveChildren(family)
        saveEvents(family)
    }

    override fun savePersons(persons: Collection<Person>) {
        val personIt = persons.iterator()
        jdbc.batchUpdate(insertPerson, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val person = personIt.next()
                preparePerson(person)
                personToStatement(statement, person)
            }

            override fun getBatchSize(): Int = persons.size
        })

        persons.forEach { person ->
            person.events.forEach { it.person = person }
        }

        val events = persons.flatMap { it.events }
        val eventsIt = events.iterator()
        jdbc.batchUpdate(insertPersonEvent, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val event = eventsIt.next()
                eventToStatement(statement, event)
                statement.setLong(6, event.person!!.id!!)
            }

            override fun getBatchSize(): Int = events.size
        })
    }

    override fun saveFamilies(families: Collection<Family>) {
        val familiesIt = families.iterator()
        jdbc.batchUpdate(insertFamily, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val family = familiesIt.next()
                prepareFamily(family)
                familyToStatement(statement, family)
            }

            override fun getBatchSize(): Int = families.size
        })

        families.forEach { family ->
            family.events.forEach { it.family = family }
        }

        val events = families.flatMap { it.events }
        val eventsIt = events.iterator()
        jdbc.batchUpdate(insertFamilyEvent, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val event = eventsIt.next()
                eventToStatement(statement, event)
                statement.setLong(6, event.family!!.id!!)
            }

            override fun getBatchSize(): Int = events.size
        })

        families.forEach { family ->
            setFamilyIds(family)
            saveChildren(family)
        }
    }

    private fun setFamilyIds(family: Family) {
        family.husband?.also { setFamily(family.id!!, it.id!!) }
        family.wife?.also { setFamily(family.id!!, it.id!!) }
        family.children.forEach { setParentFamily(family.id!!, it.id!!) }
    }

    private fun preparePerson(person: Person) {
        person.id = nextPersonId()
        person.familyId = null
        person.parentFamilyId = null
    }

    private fun personToStatement(statement: PreparedStatement, person: Person) {
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
    }

    private fun prepareFamily(family: Family) {
        family.id = nextFamilyId()
        family.husband?.also { it.familyId = family.id }
        family.wife?.also { it.familyId = family.id }
        family.children.forEach { it.parentFamilyId = family.id }
    }

    private fun familyToStatement(statement: PreparedStatement, family: Family) {
        statement.setLong(1, family.id!!)
        setLong(statement, 2, family.husband?.id)
        setLong(statement, 3, family.wife?.id)
        setString(statement, 4, family.note)
        statement.setLong(5, family.user.id!!)
        statement.setLong(6, family.lineageId!!)
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
                eventToStatement(statement, event)
                statement.setLong(6, family.id!!)
            }

            override fun getBatchSize(): Int = family.events.size
        })
    }

    private fun saveEvents(person: Person) {
        if (person.events.isEmpty()) return

        val events = person.events.iterator()
        val sql = "insert into T_PERSON_EVENT (EVENT_ID, TYPE, PLACE, NOTE, EVENT_DATE, PERSON_ID) values (?, ?, ?, ?, ?, ?)"
        jdbc.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun setValues(statement: PreparedStatement, i: Int) {
                val event = events.next()
                eventToStatement(statement, event)
                statement.setLong(6, person.id!!)
            }

            override fun getBatchSize(): Int = person.events.size
        })
    }

    private fun eventToStatement(statement: PreparedStatement, event: LifeEvent) {
        statement.setLong(1, nextEventId())
        statement.setString(2, event.type.getCode())
        setString(statement, 3, event.place)
        setString(statement, 4, event.note)
        setDate(statement, 5, event.date)
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
            it.next()
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
