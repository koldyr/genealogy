package com.koldyr.genealogy.dto

import java.time.LocalDate
import jakarta.validation.constraints.Size

/**
 * Description of the SearchDTO class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-20
 */
data class SearchDTO(
    @Size(max = 100)
    var name: String? = null,

    @Size(max = 5)
    var gender: String? = null,

    var event: SearchEventDTO? = null,

    @Size(max = 100)
    var occupation: String? = null,

    @Size(max = 100)
    var place: String? = null,

    @Size(max = 100)
    var note: String? = null
) {
    var global: Boolean? = null
    var page: PageDTO? = null
    var sort: SortDTO? = null
}

data class PageDTO (
    var size: Int = 100,
    var index: Int = 0
)

data class SortDTO (
    @Size(max = 100)
    var name: String? = null,

    @Size(max = 3)
    var order: String = "ASC"
)

data class SearchEventDTO (
    @Size(max = 100)
    var type: String = "",

    var dateFrom: LocalDate? = null,
    var dateTo: LocalDate? = null,

    @Size(max = 100)
    var place: String? = null,

    @Size(max = 100)
    var note: String? = null
)

class PageResultDTO<T>(val result: MutableList<T>) {
    var page: Int? = null
    var size: Int? = null
    var total: Long? = null
}
