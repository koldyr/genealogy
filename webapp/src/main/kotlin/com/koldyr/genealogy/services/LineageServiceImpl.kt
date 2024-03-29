package com.koldyr.genealogy.services

import java.io.ByteArrayOutputStream
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerErrorException
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.export.ExporterFactory
import com.koldyr.genealogy.importer.ImporterFactory
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.persistence.ImportRepository
import com.koldyr.genealogy.persistence.LineageRepository
import com.koldyr.genealogy.persistence.PersonRepository

/**
 * Description of class LineageServiceImpl
 *
 * @author d.halitski@gmail.com
 * @created: 2022-06-24
 */
@Service
@Transactional
class LineageServiceImpl(
    private val lineageRepository: LineageRepository,
    private val importRepository: ImportRepository,
    private val personRepository: PersonRepository,
    private val userService: UserService,
) : LineageService {

    @Transactional(readOnly = true)
    override fun findAll(): Collection<LineageDTO> = lineageRepository
        .findAllByUser(userService.currentUser())
        .map { LineageDTO(it.name, it.note, it.id) }

    @PreAuthorize("hasRole('user')")
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

        lineage.name?.also { entity.name = it }
        entity.note = lineage.note

        lineageRepository.save(entity)
    }

    override fun delete(lineageId: Long) {
        val entity = findAndCheck(lineageId)

        val persons = entity.persons
        persons.forEach {
            it.parentFamilyId = null
            it.familyId = null
        }
        personRepository.saveAll(persons)
        personRepository.flush()

        lineageRepository.delete(entity)
    }

    override fun importLineage(dataType: String, data: ByteArray, name: String, note: String?): Long {
        try {
            val importer = ImporterFactory.create(dataType)
            val lineage = importer.import(data.inputStream())

            val families = lineage.families
            val persons = lineage.persons
            lineage.families = setOf()
            lineage.persons = setOf()

            lineage.name = name
            note?.also { lineage.note = note }
            lineage.user = userService.currentUser()
            lineageRepository.saveAndFlush(lineage)

            persons.forEach { person ->
                person.lineageId = lineage.id
                person.user = lineage.user
            }
            importRepository.savePersons(persons)

            families.forEach { family ->
                family.lineageId = lineage.id
                family.user = lineage.user!!
            }
            importRepository.saveFamilies(families)

            return lineage.id!!
        } catch (e: DataAccessException) {
            LoggerFactory.getLogger(javaClass).error("", e)
            throw ServerErrorException("Unable to import lineage", e)
        } catch (e: IllegalArgumentException) {
            LoggerFactory.getLogger(javaClass).error("", e)
            throw ServerErrorException("Import failed: ${e.message}", e)
        }
    }

    @Transactional(readOnly = true)
    override fun exportLineage(lineageId: Long, dataType: String?): ByteArray {
        try {
            val lineage = findAndCheck(lineageId)
            val importer = ExporterFactory.create(dataType)

            val output = ByteArrayOutputStream()
            importer.export(lineage, output)

            return output.toByteArray()
        } catch (e: DataAccessException) {
            LoggerFactory.getLogger(javaClass).error("", e)
            throw ServerErrorException("Unable to export lineage", e)
        } catch (e: IllegalArgumentException) {
            LoggerFactory.getLogger(javaClass).error("", e)
            throw ServerErrorException("Export failed: ${e.message}", e)
        }
    }

    private fun findAndCheck(lineageId: Long): Lineage {
        val entity = lineageRepository
            .findById(lineageId)
            .orElseThrow { ResponseStatusException(NOT_FOUND, "Lineage with id '$lineageId' is not found") }

        val currentUser = userService.currentUser()
        if (currentUser.hasRole("user") && entity.user != currentUser) {
            throw ResponseStatusException(UNAUTHORIZED, "You don't have access to Lineage with id '$lineageId'")
        }

        return entity
    }
}
