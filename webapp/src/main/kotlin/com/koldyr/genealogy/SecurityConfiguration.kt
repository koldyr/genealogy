package com.koldyr.genealogy

import com.koldyr.genealogy.security.JWTAuthenticationFilter
import com.koldyr.genealogy.security.JWTAuthorizationFilter
import com.koldyr.genealogy.services.AuthenticationUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@EnableWebSecurity
open class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    @Autowired
    lateinit var authenticationUserDetailsService: AuthenticationUserDetailsService

    override fun configure(web : WebSecurity) {
        web.ignoring().antMatchers("/api/user")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/user").permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager()))
                .addFilter(JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    @Throws(java.lang.Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService<UserDetailsService>(authenticationUserDetailsService).passwordEncoder(bCryptPasswordEncoder)
    }
}