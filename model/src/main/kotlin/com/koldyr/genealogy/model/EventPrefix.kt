package com.koldyr.genealogy.model

/**
 * Description of class EventPrefix
 *
 * @author d.halitski@gmail.com
 * @created: 2019-11-06
 */
enum class EventPrefix(
        val code: String
) {
    None("-"), Before("BEF"), About("ABT"), After("AFT");

    companion object {
        fun parse(value: String?): EventPrefix? {
            if (value == null) return null
            for (prefix in entries) {
                if (value.contains(prefix.code)) {
                    return prefix
                }
            }
            return null
        }
    }
}
