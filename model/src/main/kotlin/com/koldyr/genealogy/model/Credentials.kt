package com.koldyr.genealogy.model

class Credentials {
    var username: String = ""
    var password: String = ""

    override fun toString(): String {
        return "Credentials(username='$username', password='***')"
    }
}
