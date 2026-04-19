package org.burgas.bankspring.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.attributeOrNull
import org.springframework.web.servlet.function.router

@Configuration
class SecurityRouter {

    @Bean
    fun securityRoutes() = router {
        "/api/v1/security".nest {

            GET("/csrf-token") {
                val csrfToken = it.attributeOrNull("_csrf") as CsrfToken
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(csrfToken)
            }
        }
    }
}