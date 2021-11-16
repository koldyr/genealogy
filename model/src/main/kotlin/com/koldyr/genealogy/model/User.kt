package com.koldyr.genealogy.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "T_USER")
@SequenceGenerator(name = "userIds", sequenceName = "SEQ_USER", allocationSize = 1)
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIds")
    @Column(name = "USER_ID")
    var id: Int? = null

    @Column(name = "EMAIL", nullable = false, unique = true)
    var email: String = ""

    @Column(name = "PASSWORD", nullable = false)
    var password: String = ""

    @Column(name = "NAME", nullable = false)
    var name: String = ""

    @Column(name = "SURNAME", nullable = false)
    var surName: String = ""

    override fun toString(): String {
        return "User(id=$id, email='$email', password='***', name='$name', surName='$surName')"
    }
}
