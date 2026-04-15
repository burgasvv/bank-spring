package org.burgas.bankspring.router

import org.burgas.bankspring.dto.wallet.WalletRequest
import org.burgas.bankspring.router.contract.Router
import org.burgas.bankspring.service.WalletService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.body
import org.springframework.web.servlet.function.router
import java.util.UUID

@Configuration
class WalletRouter(override val service: WalletService) : Router<WalletService> {

    @Bean
    fun walletRoutes() = router {
        "/api/v1/wallets".nest {

            GET("/by-id") {
                val walletId = UUID.fromString(it.param("walletId").orElseThrow())
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
                val walletId = UUID.fromString(it.param("walletId").orElseThrow())
                service.delete(walletId)
                ServerResponse.noContent().build()
            }
        }
    }
}