package org.burgas.bankspring.router

import org.burgas.bankspring.dto.card.CardRequest
import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.CardService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.util.UUID

@Configuration
class CardRouter(override val service: CardService) : Router<CardService> {

    @Bean
    fun cardRoutes() = router {
        "/api/v1/cards".nest {

            GET("/by-id") {
                val cardId = UUID.fromString(it.param("cardId").orElseThrow())
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
                val cardId = UUID.fromString(it.param("cardId").orElseThrow())
                service.delete(cardId)
                ServerResponse.noContent().build()
            }

            onError<Throwable> { throwable, _ ->
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