package com.koldyr.genealogy.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Description of class User
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
@Entity
@Table(name = "T_USER")
@SequenceGenerator(name = "userIds", sequenceName = "SEQ_USER", allocationSize = 1)
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIds")
    @Column(name = "USER_ID")
    var id: Long? = null

    @Column(name = "EMAIL", nullable = false, unique = true)
    @Size(max = 100)
    var email: String = ""

    @Column(name = "PASSWORD", nullable = false)
    @Size(max = 256)
    var password: String = ""

    @Column(name = "NAME", nullable = false)
    @Size(max = 32)
    var name: String = ""

    @Column(name = "SURNAME", nullable = false)
    @Size(max = 32)
    var surName: String = ""

    @ManyToOne(optional = false) @JoinColumn(name = "ROLE_ID") @JsonIgnore
    var role: Role = Role()

    override fun toString(): String = "User(id=$id, email='$email', password='***', name='$name', surName='$surName')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    fun hasRole(role: String): Boolean = this.role.name == role
}
