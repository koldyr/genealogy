package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.AuthenticatedUser
import com.koldyr.genealogy.model.Credentials
import com.koldyr.genealogy.model.User

/**
 * Description of class UserService
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
interface UserService {
    fun createUser(userModel: User)
    fun currentUser(): User
    fun login(credentials: Credentials): AuthenticatedUser
}
