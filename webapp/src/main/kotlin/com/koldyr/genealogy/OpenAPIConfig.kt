package com.koldyr.genealogy

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme

/**
 * Description of the OpenAPIConfig class
 *
 * @author d.halitski@gmail.com
 * @created 2022-06-13
 */
@Configuration
class OpenAPIConfig {

    @Bean
    fun serviceApiSecured(): OpenAPI {
        val securitySchemeName = "Bearer Auth"
        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .`in`(SecurityScheme.In.HEADER)
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .info(apiInfo())
    }

    private fun apiInfo(): Info {
        return Info()
            .title("Genealogy")
            .description("RESTfull back end for Genealogy SPA")
            .termsOfService("http://koldyr.com/genealogy/tos")
            .license(
                License()
                    .url("http://www.apache.org/licenses/LICENSE-2.0")
                    .name("Apache 2.0")
            )
            .contact(
                Contact()
                    .name("Denis Halitsky")
                    .email("me@koldyr.com")
                    .url("http://koldyr.com")

            )
    }
}
