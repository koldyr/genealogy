package com.koldyr.genealogy.model

import javax.persistence.*

@Entity
@Table(name = "T_USER")
@SequenceGenerator(name = "userIds", sequenceName = "SEQ_USER", allocationSize = 1)
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FamilyIds")
    @Column(name = "USER_ID")
    var id: Int ?= null

    @Column(name = "EMAIL")
    var email: String ?= null

    @Column(name = "PASSWORD")
    var password: String ?= null

    @Column(name = "NAME")
    var name: String ?= null

    @Column(name = "SURNAME")
    var surName: String ?= null
}