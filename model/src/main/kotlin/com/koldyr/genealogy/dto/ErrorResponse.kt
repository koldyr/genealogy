package com.koldyr.genealogy.dto

import java.time.LocalDateTime

/**
 * Description of the ErrorResponse class
 *
 * @author d.halitski@gmail.com
 * @created 2023-02-13
 */
data class ErrorResponse(
    var status: Short? = null,
    var path: String? = null,
    var timestamp: LocalDateTime? = null,
    var error: String? = null,
    var exception: String? = null,
    var trace: String? = null
)


data class ErrorDetails(
    var pointer: String? = null,
    var error: String? = null,
)
