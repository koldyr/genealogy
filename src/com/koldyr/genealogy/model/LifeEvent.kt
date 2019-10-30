package com.koldyr.genealogy.model

import java.time.LocalDate

/**
 * Description of class LifeEvent
 * @created: 2019-10-26
 */
data class LifeEvent(
        var date: LocalDate? = null,
        var place: String? = null
)
