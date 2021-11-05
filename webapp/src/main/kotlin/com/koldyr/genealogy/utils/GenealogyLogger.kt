package com.koldyr.genealogy.utils

import org.springframework.web.filter.AbstractRequestLoggingFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class GenealogyLogger : AbstractRequestLoggingFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val isFirstRequest = !isAsyncDispatch(request)
        var requestToUse = request
        if (this.isIncludePayload && isFirstRequest && request !is ContentCachingRequestWrapper) {
            requestToUse = ContentCachingRequestWrapper(request, maxPayloadLength)
        }

        val shouldLog = shouldLog(requestToUse)
        if (shouldLog && isFirstRequest) {
            beforeRequest((requestToUse), getBeforeMessage(requestToUse))
        }

        try {
            filterChain.doFilter(requestToUse as ServletRequest, response)
        } finally {
            if (shouldLog && !isAsyncStarted(requestToUse)) {
                afterRequest((requestToUse), getAfterMessage(requestToUse, response))
            }
        }
    }

    private fun getAfterMessage(request: HttpServletRequest, response: HttpServletResponse): String {
        return createMessage(request, "REQUEST DATA : ", "response status : ${response.status}")
    }

    private fun getBeforeMessage(request: HttpServletRequest): String {
        return createMessage(request, "REQUEST DATA : ", "")
    }


    override fun beforeRequest(request: HttpServletRequest, message: String) {
        this.logger.debug(message)
    }

    override fun afterRequest(request: HttpServletRequest, message: String) {
        this.logger.debug(message)
    }
}