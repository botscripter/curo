package ch.umb.curo.starter

import ch.umb.curo.starter.auth.CuroBasicAuthAuthentication
import ch.umb.curo.starter.auth.CuroOAuth2Authentication
import ch.umb.curo.starter.property.CuroProperties
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.camunda.bpm.engine.rest.security.auth.AuthenticationProvider
import org.springdoc.core.GroupedOpenApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(CuroProperties::class)
@Configuration
@ComponentScan(basePackages = ["ch.umb.curo.starter.*"])
open class CuroAutoConfiguration {

    @Autowired
    lateinit var properties: CuroProperties

    @Bean
    @ConditionalOnMissingBean
    open fun defaultAuthenticationProvider(): AuthenticationProvider {
        return when (properties.auth.type) {
            "basic" -> {
                CuroBasicAuthAuthentication()
            }
            "oauth2" -> {
                CuroOAuth2Authentication()
            }
            else -> CuroBasicAuthAuthentication()
        }
    }

    @Bean
    open fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .packagesToScan("ch.umb.curo.starter.controller")
            .group("curo-api")
            .pathsToMatch("/curo-api/**")
            .build()
    }

    @Bean
    open fun curoOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(Info().title("Curo API")
                      .description("")
                      .version("v0.0.1"))
            .components(
                Components()
                    .addSecuritySchemes("CuroBasic",
                                        SecurityScheme()
                                            .name("CuroBasic")
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("basic"))
                       )
    }

}
