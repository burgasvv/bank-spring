package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.dto.transfer.TransferRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.CardService
import org.burgas.bankspring.service.TransferService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.attributeOrNull
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.paramOrNull
import org.springframework.web.servlet.function.principalOrNull
import org.springframework.web.servlet.function.router
import java.util.UUID

@Configuration
class TransferRouter(override val service: TransferService, val cardService: CardService) : Router<TransferService> {

    @Bean
    fun transferRoutes() = router {
        "/api/v1/transfers".nest {

            filter { request, function ->
                if (request.path().equals("/api/v1/transfers/by-id", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val transferId = UUID.fromString(request.paramOrNull("transferId"))
                        val transfer = service.findEntity(transferId)

                        if (transfer.sender?.account?.wallet?.identity?.id == identityDetails.identity.id) {
                            function(request)
                        } else {
                            throw IllegalArgumentException("Identity not authorized")
                        }

                    } else {
                        throw IllegalArgumentException("identity not authenticated")
                    }

                } else if (request.path().equals("/api/v1/transfers/create", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val transferRequest = request.body<TransferRequest>()
                        val sender = cardService.findEntity(transferRequest.senderId)

                        if (sender.account?.wallet?.identity?.id == identityDetails.identity.id) {
                            request.attributes()["transferRequest"] = transferRequest
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
                val transferId = UUID.fromString(it.paramOrNull("transferId"))
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(transferId))
            }

            POST("/create") {
                val transferRequest = it.attributeOrNull("transferRequest") as TransferRequest
                service.create(transferRequest)
                ServerResponse.status(HttpStatus.OK).build()
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