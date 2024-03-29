package com.koldyr.genealogy.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Description of class Role
 *
 * @author d.halitski@gmail.com
 * @created: 2023-02-02
 */
@Entity
@Table(name = "T_ROLE")
data class Role(
    @Id
    @Column(name = "ROLE_ID")
    var id: Int? = null,

    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    var name: String = "user"
)
