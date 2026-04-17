package org.burgas.bankspring.router

import org.burgas.bankspring.dto.account.AccountRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.AccountService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.util.*

@Configuration
class AccountRouter(override val service: AccountService) : Router<AccountService> {

    @Bean
    fun accountRoutes() = router {
        "/api/v1/accounts".nest {

            GET("/by-id") {
                val accountId = UUID.fromString(it.param("accountId").orElseThrow())
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
                val accountId = UUID.fromString(it.param("accountId").orElseThrow())
                service.delete(accountId)
                ServerResponse.noContent().build()
            }
        }
    }
}