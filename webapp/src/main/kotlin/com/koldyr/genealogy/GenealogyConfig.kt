package com.koldyr.genealogy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpMethod.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.mapper.FamilyEventConverter
import com.koldyr.genealogy.mapper.PersonConverter
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyEventRepository
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.ImportRepository
import com.koldyr.genealogy.persistence.LineageRepository
import com.koldyr.genealogy.persistence.PersonEventRepository
import com.koldyr.genealogy.persistence.PersonRepository
import com.koldyr.genealogy.persistence.UserRepository
import com.koldyr.genealogy.services.AuthenticationUserDetailsService
import com.koldyr.genealogy.services.FamilyService
import com.koldyr.genealogy.services.FamilyServiceImpl
import com.koldyr.genealogy.services.LineageService
import com.koldyr.genealogy.services.LineageServiceImpl
import com.koldyr.genealogy.services.PersonService
import com.koldyr.genealogy.services.PersonServiceImpl
import com.koldyr.genealogy.services.UserService
import com.koldyr.genealogy.services.UserServiceImpl
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory


/**
 * Description of class GenealogyConfig
 *
 * @author d.halitski@gmail.com
 * @created: 2021-09-28
 */
@Configuration
@EnableAspectJAutoProxy
class GenealogyConfig {

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var familyRepository: FamilyRepository

    @Autowired
    lateinit var familyEventRepository: FamilyEventRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${security.secret}")
    private lateinit var secret: String

    @Bean
    fun personService(mapper: MapperFacade, userService: UserService, personEventRepository: PersonEventRepository): PersonService {
        return PersonServiceImpl(personRepository, personEventRepository, familyRepository, mapper, userService)
    }

    @Bean
    fun familyService(mapper: MapperFacade, userService: UserService): FamilyService {
        return FamilyServiceImpl(familyRepository, personRepository, familyEventRepository, mapper, userService)
    }

    @Bean
    fun lineageService(
        mapper: MapperFacade, userService: UserService,
        importRepository: ImportRepository,
        lineageRepository: LineageRepository
    ): LineageService {
        return LineageServiceImpl(lineageRepository, importRepository, userService)
    }

    @Bean
    fun importRepository(jdbcTemplate: JdbcTemplate): ImportRepository {
        return ImportRepository(jdbcTemplate)
    }

    @Bean
    fun userService(passwordEncoder: PasswordEncoder): UserService {
        return UserServiceImpl(userRepository, passwordEncoder, secret)
    }

    @Bean
    fun authenticationUserDetailsService(): AuthenticationUserDetailsService {
        return AuthenticationUserDetailsService(userRepository)
    }

    @Bean
    fun mapper(): MapperFacade {
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
                    .allowedMethods(GET.name, HEAD.name, POST.name, PUT.name, DELETE.name, PATCH.name)
                    .exposedHeaders(AUTHORIZATION)
            }
        }
    }
}
