package com.koldyr.genealogy.services

import java.time.LocalDate
import java.time.Month.*
import java.util.Objects.*
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root
import kotlin.reflect.full.declaredMemberProperties
import org.springframework.data.jpa.domain.Specification
import com.koldyr.genealogy.dto.SearchDTO
import com.koldyr.genealogy.dto.SearchEventDTO
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent

/**
 * Description of the PredicateBuilder class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-20
 */
class PredicateBuilder {

    fun personFilter(criteria: SearchDTO): Specification<Person>? {
        if (!hasCriteria(criteria)) {
            return null
        }

        return Specification<Person> { person, _, builder ->
            val filters = mutableListOf<Predicate>()

            criteria.name?.let {
                val namePredicate = namePredicate(person, builder, it)
                filters.add(namePredicate)
            }

            criteria.gender?.let {
                val gender = Gender.valueOf(it.uppercase())
                val predicate = builder.equal(person.get<String>("gender"), gender.name[0])
                filters.add(predicate)
            }

            criteria.occupation?.let {
                val predicate = builder.like(builder.lower(person.get("occupation")), "%${it.lowercase()}%")
                filters.add(predicate)
            }

            criteria.note?.let {
                val predicate = builder.like(builder.lower(person.get("note")), "%${it.lowercase()}%")
                filters.add(predicate)
            }

            criteria.place?.let {
                val predicate = builder.like(builder.lower(person.get("place")), "%${it.lowercase()}%")
                filters.add(predicate)
            }

            criteria.event?.let {
                val eventPredicate = eventPredicate(person, builder, it)
                filters.add(eventPredicate)
            }


            builder.and(*filters.toTypedArray())
        }
    }

    private fun namePredicate(person: Root<Person>, builder: CriteriaBuilder, name: String): Predicate {
        val nameFilters = mutableListOf<Predicate>()

        val pattern = "%${name.lowercase()}%"
        val namePath = person.get<String>("name")

        var predicate = builder.like(builder.lower(namePath.get("first")), pattern)
        nameFilters.add(predicate)

        predicate = builder.like(builder.lower(namePath.get("last")), pattern)
        nameFilters.add(predicate)

        predicate = builder.like(builder.lower(namePath.get("middle")), pattern)
        nameFilters.add(predicate)

        predicate = builder.like(builder.lower(namePath.get("maiden")), pattern)
        nameFilters.add(predicate)

        return builder.or(*nameFilters.toTypedArray())
    }

    private fun eventPredicate(person: Root<Person>, builder: CriteriaBuilder, event: SearchEventDTO): Predicate {
        val eventsJoin = person.join<Person, PersonEvent>("events")
        val typePath = eventsJoin.get<EventType>("type")

        val eventType = EventType.parse(event.type.uppercase())
        val type = builder.equal(typePath, eventType)

        val datePath = eventsJoin.get<LocalDate>("date")
        val from = event.dateFrom ?: LocalDate.of(1, JANUARY, 1)
        val till = event.dateTo ?: LocalDate.of(9999, JANUARY, 1)
        val betweenDate = builder.between(datePath, from, till)

        return builder.and(type, betweenDate)
    }

    private fun hasCriteria(criteria: SearchDTO?): Boolean {
        if (criteria == null) {
            return false
        }
        return SearchDTO::class.declaredMemberProperties
            .filter { it.name != "page" && it.name != "sort" && it.name != "universal" }
            .any { nonNull(it.get(criteria)) }
    }
}