package com.koldyr.genealogy.importer

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.bind.annotation.adapters.XmlAdapter

/**
 * Description of class LocalDateAdapter
 * @created: 2019.10.31
 */
class LocalDateAdapter : XmlAdapter<String?, LocalDate?>() {

    @Throws(Exception::class)
    override fun unmarshal(date: String?): LocalDate? {
        return if (date == null) null else LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @Throws(Exception::class)
    override fun marshal(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }
}
