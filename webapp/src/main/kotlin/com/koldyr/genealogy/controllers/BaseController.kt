package com.koldyr.genealogy.controllers

import java.time.Instant
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
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

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(request: HttpServletRequest, ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val error = ex.allErrors.first()
        val message = "${error.objectName}.${error.defaultMessage}"
        val model = buildModel(BAD_REQUEST, request.requestURI, message)
        return ResponseEntity.badRequest().body(model)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleException(request: HttpServletRequest, ex: EntityNotFoundException): ResponseEntity<Map<String, Any>> {
        val message = ex.message!!
        val model = buildModel(NOT_FOUND, request.requestURI, message)
        return ResponseEntity.status(NOT_FOUND).body(model)
    }

    private fun buildModel(status: HttpStatus, uri: String, message: String): Map<String, Any> {
        return mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to status.value(),
            "error" to status.reasonPhrase,
            "message" to message,
            "path" to uri
        )
    }
}
