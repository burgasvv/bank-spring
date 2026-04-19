package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.dto.identity.IdentityRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.IdentityService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.paramOrNull
import org.springframework.web.servlet.function.principalOrNull
import org.springframework.web.servlet.function.router
import java.util.*

@Configuration
class IdentityRouter(override val service: IdentityService) : Router<IdentityService> {

    @Bean
    fun identityRoutes() = router {
        "/api/v1/identities".nest {

            filter { request, function ->
                if (request.path().equals("/api/v1/identities/by-id", false)) {

                    val authentication = request.principalOrNull() as Authentication
                    if (authentication.isAuthenticated) {

                        val identityDetails = authentication.principal as IdentityDetails
                        val identityId = UUID.fromString(request.paramOrNull("identityId"))

                        if (identityDetails.identity.id == identityId) {
                            function(request)
                        } else {
                            throw IllegalArgumentException("Identity not authorized")
                        }

                    } else {
                        throw IllegalArgumentException("Identity not authenticated")
                    }

                } else {
                    function(request)
                }
            }

            GET("/by-id") {
                val identityId = UUID.fromString(it.paramOrNull("identityId"))
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
                val identityId = UUID.fromString(it.paramOrNull("identityId"))
                service.delete(identityId)
                ServerResponse.noContent().build()
            }

            onError<Exception> { throwable, _ ->
                ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .body(
                        ExceptionResponse(
                            HttpStatus.BAD_REQUEST.name,
                            HttpStatus.BAD_REQUEST.value(),
                            throwable.localizedMessage
                        )
                    )
            }
        }
    }
}