package com.koldyr.genealogy.util

import java.time.Instant
import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerErrorException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import com.koldyr.genealogy.dto.ErrorDetails
import com.koldyr.genealogy.export.UnsupportedExportFormatException

val LOGGER: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

/**
 * Description of class GlobalExceptionHandler
 *
 * @author d.halitski@gmail.com
 * @created: 2023-12-10
 */
@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    public override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any> {
        LOGGER.error(ex.message, ex)

        val errors = ex.allErrors.map { error ->
            "${error.objectName}.${error.defaultMessage}"
        }
        val uri = (request as ServletWebRequest).request.requestURI
        val model = buildModel(BAD_REQUEST, uri, errors)
        return badRequest().body(model)
    }

    public override fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any> {
        LOGGER.error(ex.message, ex)

        val errors = ArrayList<ErrorDetails>()
        for (violation in ex.allValidationResults) {
            val parameterName = violation.methodParameter.parameterName
            violation.resolvableErrors.forEach { error ->
                errors.add(ErrorDetails(parameterName, error.defaultMessage))
            }
        }

        val uri = (request as ServletWebRequest).request.requestURI
        val model = buildModel(BAD_REQUEST, uri, errors)
        return badRequest().body(model)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityExceptions(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val uri = (request as ServletWebRequest).request.requestURI
        val message = ex.message!!
        val model = buildModel(NOT_FOUND, uri, message)
        return ResponseEntity.status(NOT_FOUND).body(model)
    }

    @ExceptionHandler(*arrayOf(
        org.springframework.security.access.AccessDeniedException::class,
        AuthenticationException::class,
        ResponseStatusException::class
    ))
    fun handleIgnoredExceptions(ex: Exception, request: WebRequest): Any? {
        throw ex
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    fun handleAllExceptions(ex: Throwable, request: WebRequest): Map<String, Any> {
        LOGGER.error(ex.message, ex)

        val message = when (ex) {
            is UnsupportedExportFormatException, is ServerErrorException -> {
                ex.message!!
            }
            is DataAccessException -> {
                ex.javaClass.simpleName
            }
            else -> {
                ex.toString()
            }
        }

        val uri = (request as ServletWebRequest).request.requestURI
        val model = buildModel(INTERNAL_SERVER_ERROR, uri, message)
        return model
    }

    private fun buildModel(status: HttpStatus, uri: String, message: Any): Map<String, Any> {
        return mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to status.value(),
            "error" to status.reasonPhrase,
            "message" to message,
            "path" to uri
        )
    }
}
