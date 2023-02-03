package com.koldyr.genealogy.model

/**
 * Description of class Credentials
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
class Credentials {
    var username: String = ""
    var password: String = ""

    override fun toString(): String {
        return "Credentials(username='$username', password='***')"
    }
}
