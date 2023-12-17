package com.koldyr.genealogy.controllers

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.validation.annotation.Validated
import com.koldyr.genealogy.dto.ErrorResponse

/**
 * Description of class BaseController
 *
 * @author d.halitski@gmail.com
 * @created: 2023-08-19
 */
@Validated
@ApiResponse(
    responseCode = "500", description = "Internal server error",
    content = [Content(schema = Schema(implementation = ErrorResponse::class), mediaType = APPLICATION_JSON_VALUE)]
)
class BaseController {
    protected val logger = LoggerFactory.getLogger(javaClass)
}
