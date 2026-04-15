package org.burgas.bankspring.router

import org.burgas.bankspring.dto.identity.IdentityRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.IdentityService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.util.*

@Configuration
class IdentityRouter(override val service: IdentityService) : Router<IdentityService> {

    @Bean
    fun identityRoutes() = router {
        "/api/v1/identities".nest {

            GET("/by-id") {
                val identityId = UUID.fromString(it.param("identityId").orElseThrow())
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(identityId))
            }

            POST("/create") {
                val identityRequest = it.body<IdentityRequest>()
                service.create(identityRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            PUT("/update") {
                val identityRequest = it.body<IdentityRequest>()
                service.update(identityRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            DELETE("/delete") {
                val identityId = UUID.fromString(it.param("identityId").orElseThrow())
                service.delete(identityId)
                ServerResponse.noContent().build()
            }
        }
    }
}