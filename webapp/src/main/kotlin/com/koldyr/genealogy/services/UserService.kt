package com.koldyr.genealogy.services

import com.koldyr.genealogy.dto.Credentials
import com.koldyr.genealogy.dto.LineageUser
import com.koldyr.genealogy.model.User

/**
 * Description of class UserService
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
interface UserService {
    fun create(user: User)
    fun currentUser(): User
    fun login(credentials: Credentials): LineageUser
}
