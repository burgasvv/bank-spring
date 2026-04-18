package org.burgas.bankspring.router

import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.dto.transfer.TransferRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.TransferService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.util.UUID

@Configuration
class TransferRouter(override val service: TransferService) : Router<TransferService> {

    @Bean
    fun transferRoutes() = router {
        "/api/v1/transfers".nest {

            GET("/by-id") {
                val transferId = UUID.fromString(it.param("transferId").orElseThrow())
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(transferId))
            }

            POST("/create") {
                val transferRequest = it.body<TransferRequest>()
                service.create(transferRequest)
                ServerResponse.status(HttpStatus.OK).build()
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