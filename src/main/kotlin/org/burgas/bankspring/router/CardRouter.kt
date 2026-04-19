package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.dto.card.CardRequest
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.CardService
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
import java.util.UUID

@Configuration
class CardRouter(override val service: CardService) : Router<CardService> {

    @Bean
    fun cardRoutes() = router {
        "/api/v1/cards".nest {

            filter { request, function ->
                if (request.path().equals("/api/v1/cards/by-id", false)) {
                    val authentication = request.principalOrNull() as Authentication

                    if (authentication.isAuthenticated) {
                        val identityDetails = authentication.principal as IdentityDetails
                        val cardId = UUID.fromString(request.paramOrNull("cardId"))
                        val card = service.findEntity(cardId)

                        if (card.account?.wallet?.identity?.id == identityDetails.identity.id) {
                            function(request)
                        } else {
                            throw IllegalArgumentException("[Identity not authorized")
                        }

                    } else {
                        throw IllegalArgumentException("Identity not authenticated")
                    }

                } else {
                    function(request)
                }
            }

            GET("/by-id") {
                val cardId = UUID.fromString(it.paramOrNull("cardId"))
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(cardId))
            }

            POST("/create") {
                val cardRequest = it.body<CardRequest>()
                service.create(cardRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            PUT("/update") {
                val cardRequest = it.body<CardRequest>()
                service.update(cardRequest)
                ServerResponse.status(HttpStatus.OK).build()
            }

            DELETE("/delete") {
                val cardId = UUID.fromString(it.paramOrNull("cardId"))
                service.delete(cardId)
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