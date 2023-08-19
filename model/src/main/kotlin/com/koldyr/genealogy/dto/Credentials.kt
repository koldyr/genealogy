package com.koldyr.genealogy.dto

import jakarta.validation.constraints.Size

/**
 * Description of class Credentials
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
class Credentials {
    @Size(max = 100)
    var username: String = ""

    @Size(max = 100)
    var password: String = ""

    override fun toString(): String {
        return "Credentials(username='$username', password='***')"
    }
}
