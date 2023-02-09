package com.koldyr.genealogy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import com.koldyr.genealogy.security.JWTAuthorizationFilter
import com.koldyr.genealogy.services.AuthenticationUserDetailsService


/**
 * Description of class SecurityConfig
 *
 * @author d.halitski@gmail.com
 * @created: 2021-11-04
 */
@EnableWebSecurity
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Value("\${security.secret}")
    lateinit var secret: String

    @Autowired
    lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Autowired
    lateinit var authenticationUserDetailsService: AuthenticationUserDetailsService

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun jwtAuthorizationFilter(): JWTAuthorizationFilter {
        return JWTAuthorizationFilter(secret, authenticationConfiguration.authenticationManager, authenticationUserDetailsService)
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors()
            .and()
            .csrf().disable()
            .headers().disable()
            .anonymous()
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**", "/error/**", "/favicon.ico").permitAll()
            .requestMatchers(POST, "/api/user/**").permitAll()
            .requestMatchers("/api/**").authenticated()
            .and()
            .addFilter(jwtAuthorizationFilter())
            .userDetailsService(authenticationUserDetailsService)
            .sessionManagement().sessionCreationPolicy(STATELESS)

        return http.build()
    }

    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
        return GrantedAuthorityDefaults("")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
