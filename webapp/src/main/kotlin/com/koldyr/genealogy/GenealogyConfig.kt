package com.koldyr.genealogy

import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.HEAD
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.mapper.FamilyEventConverter
import com.koldyr.genealogy.mapper.PersonConverter
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyEventRepository
import com.koldyr.genealogy.persistence.PersonRepository
import com.koldyr.genealogy.persistence.UserRepository
import com.koldyr.genealogy.services.AuthenticationUserDetailsService

/**
 * Description of class GenealogyConfig
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
@Configuration
@EnableAspectJAutoProxy
class GenealogyConfig {

    @Bean
    fun authenticationUserDetailsService(userRepository: UserRepository): AuthenticationUserDetailsService = AuthenticationUserDetailsService(userRepository)

    @Bean
    fun mapper(personRepository: PersonRepository, familyEventRepository: FamilyEventRepository): MapperFacade {
        val mapperFactory = DefaultMapperFactory.Builder().build()

        mapperFactory.classMap(Family::class.java, FamilyDTO::class.java)
            .byDefault()
            .register()
        mapperFactory.classMap(Person::class.java, Person::class.java)
            .exclude("events")
            .byDefault()
            .register()

        val converterFactory = mapperFactory.converterFactory
        converterFactory.registerConverter(PersonConverter(personRepository))
        converterFactory.registerConverter(FamilyEventConverter(familyEventRepository))

        return mapperFactory.mapperFacade
    }

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {

            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods(GET.name(), HEAD.name(), POST.name(), PUT.name(), DELETE.name(), PATCH.name())
                    .exposedHeaders(AUTHORIZATION)
            }
        }
    }
}
