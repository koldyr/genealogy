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

    @Around("controllerMethods()")
    open fun logControllerCall(pjp: ProceedingJoinPoint): Any {
        val logger = LoggerFactory.getLogger(pjp.target.javaClass.name)
        logger.trace("START {}", pjp.signature.name)

        val startTime = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            val time = System.currentTimeMillis() - startTime
            logger.trace("FINISH {} in {}", pjp.signature.name, time)
        }
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    open fun controllerMethods() {}
}
