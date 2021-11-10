package com.koldyr.genealogy.util

import org.apache.commons.lang3.StringUtils.EMPTY
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.StringJoiner

/**
 * Description of class LoggerAspect
 * @created: 2021-10-13
 */
@Aspect
@Component
class LoggerAspect {

    private val COLLECTION_LIMIT: Int = 3

    @Around("controllerMethods()")
    fun logControllerCall(pjp: ProceedingJoinPoint): Any {
        val logger = LoggerFactory.getLogger(pjp.target.javaClass.name)
        logger.trace("START {}({})", pjp.signature.name, getParams(pjp))

        val startTime = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            val time = System.currentTimeMillis() - startTime
            logger.trace("FINISH {} in {}", pjp.signature.name, time)
        }
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    fun controllerMethods() {}

    private fun getParams(pjp: JoinPoint): String {
        if (pjp.args.size > 0) {
            val joiner = StringJoiner(", ")
            for (param in pjp.args) {
                if (param is Collection<*>) {
                    val size = param.size
                    if (size < COLLECTION_LIMIT) {
                        joiner.add(param.toString())
                    } else {
                        joiner.add(param.javaClass.simpleName + '#' + size)
                    }
                } else if (param is Map<*, *>) {
                    val size = param.size
                    if (size < COLLECTION_LIMIT) {
                        joiner.add(param.toString())
                    } else {
                        joiner.add(param.javaClass.simpleName + '#' + size)
                    }
                } else {
                    joiner.add(param?.toString() ?: EMPTY)
                }
            }
            return joiner.toString()
        }
        return EMPTY
    }
}
