package com.koldyr.genealogy.services

import java.util.UUID
import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.persistence.ImportRepository
import com.koldyr.genealogy.persistence.LineageRepository

@Transactional
class LineageServiceImpl(
    private val lineageRepository: LineageRepository,
    private val importRepository: ImportRepository,
    private val userService: UserService,
) : LineageService {

    @Transactional(readOnly = true)
    override fun findAll(): Collection<LineageDTO> = lineageRepository
        .findAllByUser(userService.currentUser())
        .map { LineageDTO(it.name, it.note, it.id) }

    override fun create(lineage: LineageDTO): Long {
        val entity = Lineage()
        entity.name = lineage.name ?: "${userService.currentUser().surName} ${UUID.randomUUID()}"
        entity.note = lineage.note
        entity.user = userService.currentUser()

        lineageRepository.save(entity)

        return entity.id!!
    }

    @Transactional(readOnly = true)
    override fun findById(lineageId: Long): LineageDTO {
        val lineage = findAndCheck(lineageId)
        return LineageDTO(lineage.name, lineage.note, lineage.id)
    }

    override fun update(lineageId: Long, lineage: LineageDTO) {
        val entity = findAndCheck(lineageId)

        lineage.name?.let {
            entity.name = it
        }
        entity.note = lineage.note

        lineageRepository.save(entity)
    }

    override fun delete(lineageId: Long) {
        val entity = findAndCheck(lineageId)
        lineageRepository.delete(entity)
    }

    override fun importLineage(dataType: String, data: ByteArray, name: String, note: String?): Long {
        val importer = ImporterFactory.create(dataType)
        val lineage = importer.import(data.inputStream())

        val families = HashSet(lineage.families)
        val persons = HashSet(lineage.persons)
        lineage.families = setOf()
        lineage.persons = setOf()

        lineage.name = name
        lineage.note = note
        lineage.user = userService.currentUser()
        lineageRepository.save(lineage)

        persons.forEach { person ->
            person.id = importRepository.nextPersonId()
            person.familyId = null
            person.parentFamilyId = null
            person.lineageId = lineage.id
            person.user = lineage.user

            importRepository.save(person)
        }
        
        families.forEach { family ->
            family.id = importRepository.nextFamilyId()
            family.lineageId = lineage.id
            family.user = lineage.user!!

            family.husband?.let {
                it.familyId = family.id
            }
            family.wife?.let {
                it.familyId = family.id
            }

            family.children.forEach {
                it.parentFamilyId = family.id
            }

            importRepository.save(family)
        }

        return lineage.id!!
    }

    private fun findAndCheck(lineageId: Long): Lineage {
        val entity = lineageRepository
            .findById(lineageId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Lineage with id '$lineageId' is not found") }

        if (entity.user != userService.currentUser()) {
            throw ResponseStatusException(UNAUTHORIZED, "You don't have access to Lineage with id '$lineageId'")
        }

        return entity
    }
}