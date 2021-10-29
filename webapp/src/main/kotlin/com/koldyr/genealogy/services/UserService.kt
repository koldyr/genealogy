package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User

interface UserService {
    fun createUser (userCred : User)
    fun readUserByEmail(email : String) : User

}