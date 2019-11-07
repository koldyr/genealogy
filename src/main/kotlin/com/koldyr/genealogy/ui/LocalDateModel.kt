package com.koldyr.genealogy.ui

import org.jdatepicker.AbstractDateModel
import java.time.LocalDate
import java.util.*

/**
 * Description of class LocalDateModel
 * @created: 2019-11-02
 */
class LocalDateModel: AbstractDateModel<LocalDate>() {
    override fun fromCalendar(from: Calendar?): LocalDate {
        if (from == null) {
            return LocalDate.now()
        }

        val zonedDateTime = (from as GregorianCalendar).toZonedDateTime()
        return zonedDateTime.toLocalDate()
    }

    override fun toCalendar(from: LocalDate?): Calendar {
        if (from == null) {
            return Calendar.getInstance()
        }

        val result = Calendar.getInstance()
        result.set(from.year, from.month.value - 1, from.dayOfMonth)
        return result
    }
}
