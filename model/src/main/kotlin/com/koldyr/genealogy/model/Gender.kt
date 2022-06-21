package com.koldyr.genealogy.model

/**
 * Description of class Gender
 *
 * @author d.halitski@gmail.com
 * @created: 2019-10-26
 */
enum class Gender {
    MALE, FEMALE;

    companion object {
        fun parse(value: String): Gender {
            val code = value.uppercase()
            return try {
                Gender.valueOf(code)
            } catch (e: IllegalArgumentException) {
                when (code[0]) {
                    'M' -> MALE
                    'F' -> FEMALE
                    else -> throw e
                }
            }
        }
    }
}