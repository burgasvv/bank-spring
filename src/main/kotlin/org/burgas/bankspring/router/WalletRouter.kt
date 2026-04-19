package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.dto.wallet.WalletRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.WalletService
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
class WalletRouter(override val service: WalletService) : Router<WalletService> {

    @Bean
    fun walletRoutes() = router {
        "/api/v1/wallets".nest {

            filter { request, function ->
                if (request.path().equals("/api/v1/wallets/by-id", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val walletId = UUID.fromString(request.paramOrNull("walletId"))
                        val wallet = service.findEntity(walletId)

                        if (identityDetails.identity.id == wallet.identity?.id) {
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
                val walletId = UUID.fromString(it.paramOrNull("walletId"))
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(walletId))
            }

            POST("/create") {
                val walletRequest = it.body<WalletRequest>()
                service.create(walletRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            PUT("/update") {
                val walletRequest = it.body<WalletRequest>()
                service.update(walletRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            DELETE("/delete") {
                val walletId = UUID.fromString(it.paramOrNull("walletId"))
                service.delete(walletId)
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