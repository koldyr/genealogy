package com.koldyr.genealogy

import com.koldyr.genealogy.security.JWTAuthenticationFilter
import com.koldyr.genealogy.security.JWTAuthorizationFilter
import com.koldyr.genealogy.services.AuthenticationUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler


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
        web.ignoring().antMatchers("/api/user/registration")
    }

    @Bean
    open fun jwtAuthenticationFilter(): JWTAuthenticationFilter {
        val loginAuthenticationFilter = JWTAuthenticationFilter(secret)
        loginAuthenticationFilter.setAuthenticationManager(authenticationManagerBean())
        loginAuthenticationFilter.setFilterProcessesUrl("/api/user/login")
        loginAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandle())
        return loginAuthenticationFilter
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

    @Bean
    open fun authenticationFailureHandle(): AuthenticationFailureHandler {
        return AuthenticationFailureHandler { _, response, _ ->
            response.sendError(BAD_REQUEST.value(), "username or password invalid")
        }
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
            .addFilter(jwtAuthenticationFilter())
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
