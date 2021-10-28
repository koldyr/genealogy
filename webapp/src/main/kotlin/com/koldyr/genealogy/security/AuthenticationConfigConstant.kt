package com.koldyr.genealogy.security

open class AuthenticationConfigConstant {
    val SECRET = "Java_to_Dev_Secret"
    val EXPIRATION_TIME: Long = 864000000 // 10 days

    val TOKEN_PREFIX = "Bearer "
    val HEADER_STRING = "Authorization"
    val SIGN_UP_URL = "/api/user"
}