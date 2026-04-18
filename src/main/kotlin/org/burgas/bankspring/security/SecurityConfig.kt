package org.burgas.bankspring.security

import org.burgas.bankspring.service.IdentityDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    private final val passwordEncoder: PasswordEncoder
    private final val identityDetailsService: IdentityDetailsService

    constructor(passwordEncoder: PasswordEncoder, identityDetailsService: IdentityDetailsService) {
        this.passwordEncoder = passwordEncoder
        this.identityDetailsService = identityDetailsService
    }

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val daoAuthenticationProvider = DaoAuthenticationProvider(this.identityDetailsService)
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder)
        return ProviderManager(daoAuthenticationProvider)
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity {
            cors { UrlBasedCorsConfigurationSource() }

            csrf { XorCsrfTokenRequestAttributeHandler() }

            httpBasic { RequestAttributeSecurityContextRepository() }

            authenticationManager = authenticationManager()

            authorizeHttpRequests {

                authorize("/api/v1/security/csrf-token", permitAll)

                authorize("/api/v1/identities/by-id", hasAnyAuthority("ADMIN", "USER"))
                authorize("/api/v1/identities/create", permitAll)
                authorize("/api/v1/identities/update", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/identites/delete", hasAnyAuthority("ADMIN"))

                authorize("/api/v1/wallets/by-id", hasAnyAuthority("ADMIN", "USER"))
                authorize("/api/v1/wallets/create", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/wallets/update", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/wallets/delete", hasAnyAuthority("ADMIN"))

                authorize("/api/v1/accounts/by-id", hasAnyAuthority("ADMIN", "USER"))
                authorize("/api/v1/accounts/create", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/accounts/update", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/accounts/delete", hasAnyAuthority("ADMIN"))

                authorize("/api/v1/cards/by-id", hasAnyAuthority("ADMIN", "USER"))
                authorize("/api/v1/cards/create", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/cards/update", hasAnyAuthority("ADMIN"))
                authorize("/api/v1/cards/delete", hasAnyAuthority("ADMIN"))

                authorize("/api/v1/operations/by-id", hasAnyAuthority("ADMIN", "USER"))
                authorize("/api/v1/operations/create", hasAnyAuthority("ADMIN", "USER"))

                authorize("/api/v1/transfers/by-id", hasAnyAuthority("ADMIN", "USER"))
                authorize("/api/v1/transfers/create", hasAnyAuthority("ADMIN", "USER"))
            }
        }
        return httpSecurity.build()
    }
}