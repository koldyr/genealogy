package com.koldyr.genealogy

import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.service.VendorExtension
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.koldyr.genealogy.security.Secured
import com.koldyr.genealogy.security.UnSecured

/**
 * Description of the SwaggerConfig class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-13
 */
@Configuration
class SwaggerConfig {

    @Bean
    fun serviceApiSecured(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .groupName("Authenticated")
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
        return Docket(DocumentationType.SWAGGER_2)
            .groupName("Anonymous")
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