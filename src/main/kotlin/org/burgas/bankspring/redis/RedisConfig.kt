package org.burgas.bankspring.redis

import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun identityRedisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, IdentityFullResponse> {
        val template = RedisTemplate<String, IdentityFullResponse>()
        template.connectionFactory = factory
        val serializer = JacksonJsonRedisSerializer(IdentityFullResponse::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        return template
    }

    @Bean
    fun walletRedisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, WalletFullResponse> {
        val template = RedisTemplate<String, WalletFullResponse>()
        template.connectionFactory = factory
        val serializer = JacksonJsonRedisSerializer(WalletFullResponse::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        return template
    }

    @Bean
    fun accountRedisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, AccountFullResponse> {
        val template = RedisTemplate<String, AccountFullResponse>()
        template.connectionFactory = factory
        val serializer = JacksonJsonRedisSerializer(AccountFullResponse::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        return template
    }

    @Bean
    fun cardRedisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, CardFullResponse> {
        val template = RedisTemplate<String, CardFullResponse>()
        template.connectionFactory = factory
        val serializer = JacksonJsonRedisSerializer(CardFullResponse::class.java)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = serializer
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = serializer

        return template
    }
}