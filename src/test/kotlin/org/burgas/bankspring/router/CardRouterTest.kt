package org.burgas.bankspring.router

import org.burgas.bankspring.dao.account.Account
import org.burgas.bankspring.dao.identity.Authority
import org.burgas.bankspring.dao.identity.Identity
import org.burgas.bankspring.dao.wallet.Wallet
import org.burgas.bankspring.dto.card.CardRequest
import org.burgas.bankspring.repository.AccountRepository
import org.burgas.bankspring.repository.CardRepository
import org.burgas.bankspring.repository.IdentityRepository
import org.burgas.bankspring.repository.WalletRepository
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class CardRouterTest(
    @Autowired private final val mockMvc: MockMvc,
    @Autowired private final val identityRepository: IdentityRepository,
    @Autowired private final val walletRepository: WalletRepository,
    @Autowired private final val accountRepository: AccountRepository,
    @Autowired private final val cardRepository: CardRepository
) {
    @Test
    @Order(value = 1)
    fun `create card`() {
        val newIdentity = Identity().apply {
            this.authority = Authority.ADMIN
            this.email = "admin@gmail.com"
            this.password = "admin"
            this.phone = "+78965412356"
            this.status = true
            this.firstname = "Admin"
            this.lastname = "Admin"
            this.patronymic = "Admin"
        }
        val identity = this.identityRepository.save(newIdentity)
        val newWallet = Wallet().apply {
            this.identity = identity
            this.createdAt = LocalDateTime.now()
        }
        val wallet = this.walletRepository.save(newWallet)
        val newAccount = Account().apply {
            this.wallet = wallet
            this.createdAt = LocalDateTime.now()
            this.number = 123
            this.inn = 123
            this.cpp = 123
        }
        val account = this.accountRepository.save(newAccount)
        val cardRequest = CardRequest(
            validUntil = LocalDate.now(),
            pin = "1234",
            accountId = account.id
        )
        val mapper = ObjectMapper()
        val cardRequestString = mapper.writeValueAsString(cardRequest)
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/cards/create")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(cardRequestString)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
    }

    @Test
    @Order(value = 2)
    fun `update card`() {
        val card = this.cardRepository.findCardByPin("1234").orElseThrow()
        val cardRequest = CardRequest(
            id = card.id,
            pin = "5678"
        )
        val mapper = ObjectMapper()
        val cardRequestString = mapper.writeValueAsString(cardRequest)
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put("/api/v1/cards/update")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(cardRequestString)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
    }

    @Test
    @Order(value = 3)
    fun `find card by id`() {
        val card = this.cardRepository.findCardByPin("5678").orElseThrow()
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/cards/by-id")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("cardId", card.id.toString())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andExpect { result -> println(result.response.contentAsString) }
            .andReturn()
    }

    @Test
    @Order(value = 4)
    fun `delete card`() {
        val card = this.cardRepository.findCardByPin("5678").orElseThrow()
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/cards/delete")
                    .param("cardId", card.id.toString())
                    .accept(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
        val identity = this.identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow()
        this.identityRepository.delete(identity)
    }
}