package com.koldyr.genealogy.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import com.koldyr.genealogy.model.User

/**
 * Description of the LineageUser class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-17
 */
class LineageUser(private val user: User) : UserDetails {

    var token: String = ""

    override fun getAuthorities(): List<GrantedAuthority> = listOf(SimpleGrantedAuthority(user.role.name))

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

    override fun toString(): String = "${user.name} ${user.surName}"

    fun userId(): Long = user.id!!
}
