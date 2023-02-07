package com.koldyr.genealogy.util

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.*
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerErrorException
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
import com.koldyr.genealogy.export.UnsupportedExportFormatException

/**
 * Description of the InternalExceptionHandler class
 *
 * @author d.halitski@gmail.com
 * @created 2023-02-07
 */
@Component
class InternalExceptionResolver : AbstractHandlerExceptionResolver() {

    override fun doResolveException(request: HttpServletRequest, response: HttpServletResponse, handler: Any?, ex: Exception): ModelAndView? {
        when (ex) {
            is UnsupportedExportFormatException, is ServerErrorException -> {
                response.sendError(SC_INTERNAL_SERVER_ERROR, ex.message)
            }
            is DataAccessException -> {
                response.sendError(SC_INTERNAL_SERVER_ERROR, ex.javaClass.simpleName)
            }
            else -> {
                return super.resolveException(request, response, handler, ex)
            }
        }
        return ModelAndView()
    }
}
