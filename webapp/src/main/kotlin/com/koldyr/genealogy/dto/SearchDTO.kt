package com.koldyr.genealogy.dto

import java.time.LocalDate

/**
 * Description of the SearchDTO class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-20
 */
data class SearchDTO(
    var name: String? = null,
    var gender: String? = null,
    var event: SearchEventDTO? = null,
    var occupation: String? = null,
    var place: String? = null,
    var note: String? = null
) {
    var universal: Boolean = false
    var page: PageDTO? = null
    var sort: SortDTO? = null
}

data class PageDTO (
    var size: Int = 100,
    var index: Int = 0
)

data class SortDTO (
    var name: String? = null,
    var order: String = "ASC"
)

data class SearchEventDTO (
    var type: String = "",
    var dateFrom: LocalDate? = null,
    var dateTo: LocalDate? = null,
    var place: String? = null
)

class PageResultDTO<T>(val result: List<T>) {
    var page: Int? = null
    var size: Int? = null
    var total: Long? = null
}
