package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.dto.account.AccountRequest
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.AccountService
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
class AccountRouter(override val service: AccountService) : Router<AccountService> {

    @Bean
    fun accountRoutes() = router {
        "/api/v1/accounts".nest {

            filter { request, function ->
                if (request.path().equals("/api/v1/accounts/by-id", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val accountId = UUID.fromString(request.paramOrNull("accountId"))
                        val account = service.findEntity(accountId)

                        if (account.wallet?.identity?.id == identityDetails.identity.id) {
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
                val accountId = UUID.fromString(it.paramOrNull("accountId"))
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(accountId))
            }

            POST("/create") {
                val accountRequest = it.body<AccountRequest>()
                service.create(accountRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            PUT("/update") {
                val accountRequest = it.body<AccountRequest>()
                service.update(accountRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            DELETE("/delete") {
                val accountId = UUID.fromString(it.paramOrNull("accountId"))
                service.delete(accountId)
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