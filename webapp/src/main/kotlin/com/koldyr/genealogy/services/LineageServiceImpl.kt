package com.koldyr.genealogy.services

import org.springframework.http.HttpStatus.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import com.koldyr.genealogy.dto.LineageDTO
import com.koldyr.genealogy.model.Lineage
import com.koldyr.genealogy.persistence.LineageRepository

@Transactional
class LineageServiceImpl(
    private val lineageRepository: LineageRepository,
    private val userService: UserService,
) : LineageService {

    @Transactional(readOnly = true)
    override fun findAll(): Collection<LineageDTO> = lineageRepository
        .findAllByUser(userService.currentUser())
        .map { LineageDTO(it.name, it.note, it.id) }

    override fun create(lineage: LineageDTO): Long {
        val entity = Lineage()
        entity.name = lineage.name
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
        entity.name = lineage.name
        entity.note = lineage.note

        lineageRepository.save(entity)
    }

    override fun delete(lineageId: Long) {
        val entity = findAndCheck(lineageId)
        lineageRepository.delete(entity)
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