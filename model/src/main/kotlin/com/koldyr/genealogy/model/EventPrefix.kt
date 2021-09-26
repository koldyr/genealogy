package com.koldyr.genealogy.model

/**
 * Description of class EventPrefix
 * @created: 2019-11-06
 */
enum class EventPrefix(
        val code: String
) {
    None("-"), Before("BEF"), About("ABT"), After("AFT");

    companion object {
        fun parse(value: String): EventPrefix? {
            for (prefix in values()) {
                if (value.contains(prefix.code)) {
                    return prefix
                }
            }
            return null
        }
    }
}
