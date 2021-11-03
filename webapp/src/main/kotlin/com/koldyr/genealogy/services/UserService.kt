package com.koldyr.genealogy.services

import com.koldyr.genealogy.model.User

interface UserService {
    fun createUser (userModel : User)
    fun readUserByEmail(email : String) : User
    fun currentUser() : User
}