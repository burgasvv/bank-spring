package org.burgas.bankspring.router

import org.burgas.bankspring.dao.identity.Authority
import org.burgas.bankspring.dto.identity.IdentityRequest
import org.burgas.bankspring.repository.IdentityRepository
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

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation::class)
class IdentityRouterTest(
    @Autowired private final val mockMvc: MockMvc,
    @Autowired private final val identityRepository: IdentityRepository
) {
    @Test
    @Order(value = 1)
    fun `create identity`() {
        val identityRequest = IdentityRequest(
            authority = Authority.ADMIN,
            email = "admin@gmail.com",
            password = "admin",
            phone = "+79456324512",
            status = true,
            firstname = "Admin",
            lastname = "Admin",
            patronymic = "Admin"
        )
        val mapper = ObjectMapper()
        val identityRequestString = mapper.writeValueAsString(identityRequest)
        this.mockMvc
            .perform(
                MockMvcRequestBuilders
                    .post("/api/v1/identities/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(identityRequestString)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
    }

    @Test
    @Order(value = 2)
    fun `update identity`() {
        val identity = this.identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow()
        val identityRequest = IdentityRequest(
            id = identity.id,
            phone = "+79456324512"
        )
        val mapper = ObjectMapper()
        val identityRequestString = mapper.writeValueAsString(identityRequest)
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.put("/api/v1/identities/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(identityRequestString)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
    }

    @Test
    @Order(value = 3)
    fun `find identity by id`() {
        val identity = this.identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow()
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/identities/by-id")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("identityId", identity.id.toString())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andDo { result -> println(result.response.contentAsString) }
            .andReturn()
    }

    @Test
    @Order(value = 4)
    fun `delete identity`() {
        val identity = this.identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow()
        this.mockMvc
            .perform(
                MockMvcRequestBuilders.delete("/api/v1/identities/delete")
                    .accept(MediaType.APPLICATION_JSON)
                    .param("identityId", identity.id.toString())
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
            )
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
    }
}