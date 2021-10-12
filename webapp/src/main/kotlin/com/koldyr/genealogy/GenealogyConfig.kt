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
import com.koldyr.genealogy.services.FamilyService
import com.koldyr.genealogy.services.FamilyServiceImpl
import com.koldyr.genealogy.services.PersonService
import com.koldyr.genealogy.services.PersonServiceImpl
import ma.glasnost.orika.MapperFacade
import ma.glasnost.orika.impl.DefaultMapperFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.VendorExtension
import springfox.documentation.spi.DocumentationType.*
import springfox.documentation.spring.web.plugins.Docket


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

    @Bean
    open fun personService(mapper: MapperFacade): PersonService {
        return PersonServiceImpl(personRepository, personEventRepository, mapper)
    }

    @Bean
    open fun familyService(mapper: MapperFacade): FamilyService {
        return FamilyServiceImpl(familyRepository, personRepository, familyEventRepository, mapper)
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
    open fun api(): Docket {
        return Docket(SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
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
