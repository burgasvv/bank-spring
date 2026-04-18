package org.burgas.bankspring.router

import org.burgas.bankspring.dto.exception.ExceptionResponse
import org.burgas.bankspring.dto.operation.OperationRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.OperationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.util.UUID

@Configuration
class OperationRouter(override val service: OperationService) : Router<OperationService> {

    @Bean
    fun operationRoutes() = router {
        "/api/v1/operations".nest {

            GET("/by-id") {
                val operationId = UUID.fromString(it.param("operationId").orElseThrow())
                ServerResponse
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(service.findById(operationId))
            }

            POST("/create") {
                val operationRequest = it.body<OperationRequest>()
                service.create(operationRequest)
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