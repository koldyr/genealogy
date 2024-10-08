package com.koldyr.genealogy.model

/**
 * Description of class EventType
 *
 * @author d.halitski@gmail.com
 * @created: 2019-11-07
 */
@Suppress("SpellCheckingInspection")
enum class EventType(
    private val code: String
) {
    Birth("BIRT"), Death("DEAT"), Engagement("ENGA"), Marriage("MARR"), Divorce("DIV"), Adoption("ADOP"), Christening("CHRI"),
    Relocation("RESI"), Education("EDUC"), Emigration("EMIG"), GetJob("OCCU"), Graduation("GRAD"), Retirement("RETI"), Immigration("IMMI");

    fun getCode(): String = code

    companion object {
        fun isEvent(value: String): Boolean {
            for (type in entries) {
                if (value.endsWith(type.code) || (value.endsWith(type.name))) return true
            }
            return false
        }

        fun parse(value: String): EventType {
            for (type in entries) {
                if (value.endsWith(type.code) || (value.endsWith(type.name))) return type
            }
            return Birth
        }
    }
}
