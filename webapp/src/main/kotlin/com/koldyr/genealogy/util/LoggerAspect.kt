package com.koldyr.genealogy.util

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Description of class LoggerAspect
 * @created: 2021-10-13
 */
@Aspect
@Component
open class LoggerAspect {

    @Around("controllerMethod()")
    fun logControllerCall(pjp: ProceedingJoinPoint) {
        val logger = LoggerFactory.getLogger(pjp.target.javaClass.name)
        logger.trace("START {}", pjp.signature.name)
        try {
            pjp.proceed()
        } finally {
            logger.trace("FINISH {}", pjp.signature.name)
        }
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    fun controllerMethod() {}
}