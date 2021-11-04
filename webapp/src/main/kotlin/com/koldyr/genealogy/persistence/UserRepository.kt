package com.koldyr.genealogy.persistence

import com.koldyr.genealogy.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("userRepository")
interface UserRepository : CrudRepository<User, Int>{
    fun findByEmail(email : String) : Optional<User>
}