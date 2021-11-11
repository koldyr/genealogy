package com.koldyr.genealogy

import com.koldyr.genealogy.security.JWTAuthorizationFilter
import com.koldyr.genealogy.services.AuthenticationUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder


@EnableWebSecurity
@Configuration
open class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Value("\${security.secret}")
    lateinit var secret: String

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var authenticationUserDetailsService: AuthenticationUserDetailsService

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/api/user/**")
    }

    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    open fun jwtAuthorizationFilter(): JWTAuthorizationFilter {
        return JWTAuthorizationFilter(secret, authenticationManagerBean())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/**").authenticated()
            .and()
            .addFilter(jwtAuthorizationFilter())
            .sessionManagement().sessionCreationPolicy(STATELESS)
    }

    @Throws(java.lang.Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService<UserDetailsService>(authenticationUserDetailsService)
            .passwordEncoder(passwordEncoder)
    }
}
