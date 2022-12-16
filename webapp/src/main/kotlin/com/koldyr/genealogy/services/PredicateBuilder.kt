package com.koldyr.genealogy.services

import java.time.LocalDate
import java.time.Month.*
import java.util.Objects.*
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import kotlin.reflect.full.declaredMemberProperties
import org.springframework.data.jpa.domain.Specification
import com.koldyr.genealogy.dto.SearchDTO
import com.koldyr.genealogy.dto.SearchEventDTO
import com.koldyr.genealogy.model.EventType
import com.koldyr.genealogy.model.Gender
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.model.PersonEvent
import com.koldyr.genealogy.model.PersonNames
import com.koldyr.genealogy.model.User

/**
 * Description of the PredicateBuilder class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-20
 */
class PredicateBuilder {

    fun personFilter(lineageId: Long, criteria: SearchDTO, userId: Long): Specification<Person>? {
        if (!hasCriteria(criteria)) {
            return null
        }

        return Specification<Person> { person, _, builder ->
            val filters = mutableListOf<Predicate>()

            criteria.name?.also {
                val namePredicate = namePredicate(person, builder, it)
                filters.add(namePredicate)
            }

            criteria.gender?.also {
                val gender = Gender.parse(it)
                val predicate = builder.equal(person.get<Gender>("gender"), gender)
                filters.add(predicate)
            }

            criteria.occupation?.also {
                val predicate = builder.like(builder.lower(person.get("occupation")), "%${it.lowercase()}%")
                filters.add(predicate)
            }

            criteria.note?.also {
                val predicate = builder.like(builder.lower(person.get("note")), "%${it.lowercase()}%")
                filters.add(predicate)
            }

            criteria.place?.also {
                val predicate = builder.like(builder.lower(person.get("place")), "%${it.lowercase()}%")
                filters.add(predicate)
            }

            criteria.event?.also {
                val eventPredicate = eventPredicate(person, builder, it)
                filters.add(eventPredicate)
            }

            val global = criteria.global ?: false
            if (!global) {
                filters.add(builder.equal(person.get<User>("user").get<Long>("id"), userId))
                filters.add(builder.equal(person.get<Long>("lineageId"), lineageId))
            }

            builder.and(*filters.toTypedArray())
        }
    }

    private fun namePredicate(person: Root<Person>, builder: CriteriaBuilder, name: String): Predicate {
        val nameFilters = mutableListOf<Predicate>()

        val pattern = "%${name.lowercase()}%"
        val namePath = person.get<PersonNames>("name")

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
        val eventFilters = mutableListOf<Predicate>()

        val eventsJoin = person.join<Person, PersonEvent>("events")
        val typePath = eventsJoin.get<EventType>("type")

        val eventType = EventType.parse(event.type.uppercase())
        val type = builder.equal(typePath, eventType)
        eventFilters.add(type)

        if (nonNull(event.dateFrom) || nonNull(event.dateTo)) {
            val datePath = eventsJoin.get<LocalDate>("date")
            val from = event.dateFrom ?: LocalDate.of(1, JANUARY, 1)
            val till = event.dateTo ?: LocalDate.of(9999, JANUARY, 1)
            val betweenDate = builder.between(datePath, from, till)
            val between = builder.and(type, betweenDate)
            eventFilters.add(between)
        }

        event.note?.also {
            val pattern = "%${it.lowercase()}%"
            val note = builder.like(builder.lower(eventsJoin.get("note")), pattern)
            eventFilters.add(note)
        }

        event.place?.also {
            val pattern = "%${it.lowercase()}%"
            val place = builder.like(builder.lower(eventsJoin.get("place")), pattern)
            eventFilters.add(place)
        }

        return builder.and(*eventFilters.toTypedArray())
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
