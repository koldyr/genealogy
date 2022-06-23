package com.koldyr.genealogy.persistence

import java.util.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.User

@Repository("userRepository")
interface UserRepository : CrudRepository<User, Long>{
    fun findByEmail(email : String) : Optional<User>
}