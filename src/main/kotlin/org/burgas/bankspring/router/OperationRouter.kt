package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.dto.operation.OperationRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.CardService
import org.burgas.bankspring.service.OperationService
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
class OperationRouter(override val service: OperationService, val cardService: CardService) : Router<OperationService> {

    @Bean
    fun operationRoutes() = router {
        "/api/v1/operations".nest {

            filter { request, function ->
                if (request.path().equals("/api/v1/operations/by-id", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val operationId = UUID.fromString(request.paramOrNull("operationId"))
                        val operation = service.findEntity(operationId)

                        if (operation.card?.account?.wallet?.identity?.id == identityDetails.identity.id) {
                            function(request)
                        } else {
                            throw IllegalArgumentException("Identity not authorized")
                        }

                    } else {
                        throw IllegalArgumentException("Identity not authenticated")
                    }

                } else if (request.path().equals("/api/v1/operations/create", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val operationRequest = request.body<OperationRequest>()
                        val card = cardService.findEntity(operationRequest.cardId)

                        if (card.account?.wallet?.identity?.id == identityDetails.identity.id) {
                            request.attributes()["operationRequest"] = operationRequest
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
                val operationId = UUID.fromString(it.paramOrNull("operationId"))
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(operationId))
            }

            POST("/create") {
                val operationRequest = it.attributeOrNull("operationRequest") as OperationRequest
                service.create(operationRequest)
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