package com.koldyr.genealogy.util

import org.springframework.dao.DataAccessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ServerErrorException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import com.koldyr.genealogy.export.UnsupportedExportFormatException

/**
 * Description of the InternalExceptionHandler class
 *
 * @author d.halitski@gmail.com
 * @created 2023-02-07
 */
@ControllerAdvice
class InternalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(
        value = [
            UnsupportedExportFormatException::class,
            ServerErrorException::class
        ]
    )
    fun handleException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.internalServerError().body(ex.message)
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataException(ex: DataAccessException): ResponseEntity<String> {
        return ResponseEntity.internalServerError().body(DataAccessException::class.simpleName)
    }
}
