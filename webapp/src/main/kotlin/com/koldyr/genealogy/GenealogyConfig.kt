package com.koldyr.genealogy

import com.koldyr.genealogy.dto.FamilyDTO
import com.koldyr.genealogy.mapper.FamilyEventConverter
import com.koldyr.genealogy.mapper.PersonConverter
import com.koldyr.genealogy.model.Family
import com.koldyr.genealogy.model.Person
import com.koldyr.genealogy.persistence.*
import com.koldyr.genealogy.services.*
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import java.util.*


/**
 * Description of class GenealogyConfig
 * @created: 2021-09-28
 */
@Configuration
open class GenealogyConfig {

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

    @Bean
    open fun personService(mapper: MapperFacade): PersonService {
        return PersonServiceImpl(personRepository, personEventRepository, familyRepository, mapper)
    }

    @Bean
    open fun familyService(mapper: MapperFacade): FamilyService {
        return FamilyServiceImpl(familyRepository, personRepository, familyEventRepository, mapper)
    }

    @Bean
    open fun userService(bCryptPasswordEncoder: BCryptPasswordEncoder): UserService {
        return UserServiceImpl(userRepository, bCryptPasswordEncoder)
    }

    @Bean
    open fun authenticationUserDetailsService(userService: UserService) : AuthenticationUserDetailsService {
        return AuthenticationUserDetailsService(userService)
    }

    @Bean
    open fun mapper(): MapperFacade {
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
    open fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods(HttpMethod.GET.name, HttpMethod.HEAD.name, HttpMethod.POST.name, HttpMethod.PUT.name, HttpMethod.DELETE.name)
            }
        }
    }


    @Bean
    open fun serviceApiSecured(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("secured")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.koldyr.genealogy.controllers.secured"))
                .build()
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .securitySchemes(mutableListOf(apiKey()) as List<SecurityScheme>?)
                .securityContexts(Arrays.asList(securityContext()))
    }

    @Bean
    open fun serviceApiLogin(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("login")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.koldyr.genealogy.controllers.unsecured"))
                .build()
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
    }

    private fun apiKey(): ApiKey {
        return ApiKey("JWT", "Authorization", "header")
    }

    private fun securityContext(): SecurityContext? {
        return SecurityContext.builder().securityReferences(defaultAuth()).build()
    }

    private fun defaultAuth(): List<SecurityReference?>? {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        val authorizationScopes: Array<AuthorizationScope?> = arrayOfNulls<AuthorizationScope>(1)
        authorizationScopes[0] = authorizationScope
        return Arrays.asList(SecurityReference("JWT", authorizationScopes))
    }


    private fun apiInfo(): ApiInfo {
        val title = "Genealogy"
        val description = "RESTfull back end for Genealogy SPA"
        val vendorExtensions: List<VendorExtension<*>> = mutableListOf()
        val termsOfServiceUrl = "http://koldyr.com/genealogy/tos"
        val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0"
        return ApiInfo(title, description, "2.0", termsOfServiceUrl, null, "Apache 2.0", licenseUrl, vendorExtensions)
    }

    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }
}
