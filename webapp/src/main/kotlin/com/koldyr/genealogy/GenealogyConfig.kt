package com.koldyr.genealogy

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.mapper.FamilyEventConverter
import com.koldyr.genealogy.mapper.PersonConverter
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.FamilyEventRepository
import com.koldyr.genealogy.persistence.FamilyRepository
import com.koldyr.genealogy.persistence.PersonEventRepository
import com.koldyr.genealogy.persistence.PersonRepository
import com.koldyr.genealogy.persistence.UserRepository
import com.koldyr.genealogy.security.Secured
import com.koldyr.genealogy.security.UnSecured
import com.koldyr.genealogy.services.AuthenticationUserDetailsService
import com.koldyr.genealogy.services.FamilyService
import com.koldyr.genealogy.services.FamilyServiceImpl
import com.koldyr.genealogy.services.PersonService
import com.koldyr.genealogy.services.PersonServiceImpl
import com.koldyr.genealogy.services.UserService
import com.koldyr.genealogy.services.UserServiceImpl
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.service.VendorExtension
import springfox.documentation.spi.DocumentationType.SWAGGER_2
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket


/**
 * Description of class GenealogyConfig
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
    lateinit var personEventRepository: PersonEventRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Value("\${security.secret}")
    private lateinit var secret: String

    @Bean
    fun personService(mapper: MapperFacade, userService: UserService): PersonService {
        return PersonServiceImpl(personRepository, personEventRepository, familyRepository, mapper, userService)
    }

    @Bean
    fun familyService(mapper: MapperFacade, userService: UserService): FamilyService {
        return FamilyServiceImpl(familyRepository, personRepository, familyEventRepository, mapper, userService)
    }

    @Bean
    fun userService(passwordEncoder: PasswordEncoder): UserService {
        return UserServiceImpl(userRepository, passwordEncoder, secret)
    }

    @Bean
    fun authenticationUserDetailsService() : AuthenticationUserDetailsService {
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
                        .allowedMethods(HttpMethod.GET.name, HttpMethod.HEAD.name, HttpMethod.POST.name, HttpMethod.PUT.name, HttpMethod.DELETE.name, HttpMethod.PATCH.name)
                        .exposedHeaders(HttpHeaders.AUTHORIZATION)
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun serviceApiSecured(): Docket {
        return Docket(SWAGGER_2)
                .groupName("secured")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Secured::class.java))
                .build()
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .securitySchemes(listOf(apiKey()) as List<SecurityScheme>)
                .securityContexts(listOf(securityContext()))
    }

    @Bean
    fun serviceApiLogin(): Docket {
        return Docket(SWAGGER_2)
                .groupName("login")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(UnSecured::class.java))
                .build()
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
    }

    private fun apiKey(): ApiKey {
        return ApiKey("JWT", "Authorization", "header")
    }

    private fun securityContext(): SecurityContext {
        return SecurityContext.builder().securityReferences(defaultAuth()).build()
    }

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes: Array<AuthorizationScope> = arrayOf(authorizationScope)
        return listOf(SecurityReference("JWT", authorizationScopes))
    }

    private fun apiInfo(): ApiInfo {
        val title = "Genealogy"
        val description = "RESTfull back end for Genealogy SPA"
        val vendorExtensions: List<VendorExtension<*>> = mutableListOf()
        val termsOfServiceUrl = "http://koldyr.com/genealogy/tos"
        val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0"
        return ApiInfo(title, description, "2.0", termsOfServiceUrl, null, "Apache 2.0", licenseUrl, vendorExtensions)
    }
}
