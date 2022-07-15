package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.LineageDTO

/**
 * Description of the LineageService class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
interface LineageService {
    fun findAll(): Collection<LineageDTO>
    fun create(lineage: LineageDTO): Long
    fun findById(lineageId: Long): LineageDTO
    fun update(lineageId: Long, lineage: LineageDTO)
    fun delete(lineageId: Long)
    
    fun importLineage(dataType: String, data: ByteArray, name: String, note: String?): Long
    fun exportLineage(lineageId: Long, dataType: String?): ByteArray
}