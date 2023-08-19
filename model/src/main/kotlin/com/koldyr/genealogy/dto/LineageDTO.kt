package com.koldyr.genealogy.dto

import jakarta.validation.constraints.Size

/**
 * Description of the LineageDTO class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-23
 */
data class LineageDTO(
    @Size(max = 256)
    var name: String? = null,

    @Size(max = 256)
    var note: String? = null,

    var id: Long? = null
)
