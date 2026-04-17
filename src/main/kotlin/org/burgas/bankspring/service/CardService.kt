package org.burgas.bankspring.service

import org.burgas.bankspring.dao.card.Card
import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.card.CardRequest
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.mapper.CardMapper
import org.burgas.bankspring.redis.RedisCacheHandler
import org.burgas.bankspring.service.contract.CrudService
import org.burgas.bankspring.util.KeyUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class CardService : org.burgas.bankspring.service.contract.Service,
    CrudService<CardRequest, Card, CardFullResponse>, RedisCacheHandler<Card> {

    private final val cardMapper: CardMapper

    @Qualifier(value = "identityRedisTemplate")
    private final val identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>

    @Qualifier(value = "walletRedisTemplate")
    private final val walletRedisTemplate: RedisTemplate<String, WalletFullResponse>

    @Qualifier(value = "accountRedisTemplate")
    private final val accountRedisTemplate: RedisTemplate<String, AccountFullResponse>

    @Qualifier(value = "cardRedisTemplate")
    private final val cardRedisTemplate: RedisTemplate<String, CardFullResponse>

    constructor(
        cardMapper: CardMapper,
        identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>,
        walletRedisTemplate: RedisTemplate<String, WalletFullResponse>,
        accountRedisTemplate: RedisTemplate<String, AccountFullResponse>,
        cardRedisTemplate: RedisTemplate<String, CardFullResponse>
    ) {
        this.cardMapper = cardMapper
        this.identityRedisTemplate = identityRedisTemplate
        this.walletRedisTemplate = walletRedisTemplate
        this.accountRedisTemplate = accountRedisTemplate
        this.cardRedisTemplate = cardRedisTemplate
    }

    override fun handleCache(entity: Card) {
        val cardKey = KeyUtil.CARD_KEY.format(entity.id)
        if (this.cardRedisTemplate.hasKey(cardKey)) this.cardRedisTemplate.delete(cardKey)

        val account = entity.account
        if (account != null) {
            val accountKey = KeyUtil.ACCOUNT_KEY.format(account.id)
            if (this.accountRedisTemplate.hasKey(accountKey)) this.accountRedisTemplate.delete(accountKey)

            val wallet = account.wallet
            if (wallet != null) {
                val walletKey = KeyUtil.WALLET_KEY.format(wallet.id)
                if (this.walletRedisTemplate.hasKey(walletKey)) this.walletRedisTemplate.delete(walletKey)

                val identity = wallet.identity
                if (identity != null) {
                    val identityKey = KeyUtil.IDENTITY_KEY.format(identity.id)
                    if (this.identityRedisTemplate.hasKey(identityKey)) this.identityRedisTemplate.delete(identityKey)
                }
            }
        }
    }

    override fun findEntity(id: UUID): Card {
        return this.cardMapper.cardRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException("Card not found") }
    }

    override fun findById(id: UUID): CardFullResponse {
        val cardKey = KeyUtil.CARD_KEY.format(id)
        val cardFromRedis = this.cardRedisTemplate.opsForValue().get(cardKey)
        if (cardFromRedis != null) {
            return cardFromRedis
        } else {
            val cardFullResponse = this.cardMapper.toFullResponse(this.findEntity(id))
            this.cardRedisTemplate.opsForValue().set(cardKey, cardFullResponse)
            return cardFullResponse
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun create(request: CardRequest) {
        val card = this.cardMapper.cardRepository.save(this.cardMapper.toEntity(request))
        this.handleCache(card)
        val cardFullResponse = this.cardMapper.toFullResponse(card)
        val cardKey = KeyUtil.CARD_KEY.format(cardFullResponse.id)
        this.cardRedisTemplate.opsForValue().set(cardKey, cardFullResponse)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun update(request: CardRequest) {
        if (request.id == null) throw IllegalArgumentException("Card Request id is null")
        val card = this.cardMapper.cardRepository.save(this.cardMapper.toEntity(request))
        this.handleCache(card)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun delete(id: UUID) {
        val findEntity = this.findEntity(id)
        this.cardMapper.cardRepository.delete(findEntity)
        this.handleCache(findEntity)
    }
}