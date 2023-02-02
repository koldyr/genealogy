package com.koldyr.genealogy.persistence

import java.util.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import com.koldyr.genealogy.model.Role

@Repository("roleRepository")
interface RoleRepository : CrudRepository<Role, Long>
