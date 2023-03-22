package com.koldyr.genealogy.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.User

/**
 * Description of class PersonRepository
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-25
 */
@Repository("personRepository")
interface PersonRepository : JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    fun findAllByUserAndLineageId(user: User, lineageId: Long): List<Person>
}
