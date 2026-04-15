package org.burgas.bankspring.service

import org.burgas.bankspring.dao.wallet.Wallet
import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.dto.wallet.WalletRequest
import org.burgas.bankspring.mapper.WalletMapper
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
class WalletService : org.burgas.bankspring.service.contract.Service,
    CrudService<WalletRequest, Wallet, WalletFullResponse>, RedisCacheHandler<Wallet> {

    private final val walletMapper: WalletMapper

    @Qualifier(value = "identityRedisTemplate")
    private final val identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>

    @Qualifier(value = "walletRedisTemplate")
    private final val walletRedisTemplate: RedisTemplate<String, WalletFullResponse>

    @Qualifier(value = "accountRedisTemplate")
    private final val accountRedisTemplate: RedisTemplate<String, AccountFullResponse>

    @Qualifier(value = "cardRedisTemplate")
    private final val cardRedisTemplate: RedisTemplate<String, CardFullResponse>

    constructor(
        walletMapper: WalletMapper,
        identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>,
        walletRedisTemplate: RedisTemplate<String, WalletFullResponse>,
        accountRedisTemplate: RedisTemplate<String, AccountFullResponse>,
        cardRedisTemplate: RedisTemplate<String, CardFullResponse>
    ) {
        this.walletMapper = walletMapper
        this.identityRedisTemplate = identityRedisTemplate
        this.walletRedisTemplate = walletRedisTemplate
        this.accountRedisTemplate = accountRedisTemplate
        this.cardRedisTemplate = cardRedisTemplate
    }

    override fun handleCache(entity: Wallet) {
        val walletKey = KeyUtil.WALLET_KEY.format(entity.id)
        if (this.walletRedisTemplate.hasKey(walletKey)) this.walletRedisTemplate.delete(walletKey)

        val identity = entity.identity
        if (identity != null) {
            val identityKey = KeyUtil.IDENTITY_KEY.format(identity.id)
            if (this.identityRedisTemplate.hasKey(identityKey)) this.identityRedisTemplate.delete(identityKey)
        }

        val accounts = entity.accounts
        if (!accounts.isEmpty()) {
            accounts.forEach { account ->
                val accountKey = KeyUtil.ACCOUNT_KEY.format(account.id)
                if (this.accountRedisTemplate.hasKey(accountKey)) this.accountRedisTemplate.delete(accountKey)

                val card = account.card
                if (card != null) {
                    val cardKey = KeyUtil.CARD_KEY.format(card.id)
                    if (this.cardRedisTemplate.hasKey(cardKey)) this.cardRedisTemplate.delete(cardKey)
                }
            }
        }
    }

    override fun findEntity(id: UUID): Wallet {
        return this.walletMapper.walletRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException("Wallet not found") }
    }

    override fun findById(id: UUID): WalletFullResponse {
        val walletKey = KeyUtil.WALLET_KEY.format(id)
        val walletFromRedis = this.walletRedisTemplate.opsForValue().get(walletKey)
        if (walletFromRedis != null) {
            return walletFromRedis
        } else {
            val walletFullResponse = this.walletMapper.toFullResponse(this.findEntity(id))
            this.walletRedisTemplate.opsForValue().set(walletKey, walletFullResponse)
            return walletFullResponse
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun create(request: WalletRequest) {
        val wallet = this.walletMapper.walletRepository.save(this.walletMapper.toEntity(request))
        this.handleCache(wallet)
        val walletFullResponse = this.walletMapper.toFullResponse(wallet)
        val walletKey = KeyUtil.WALLET_KEY.format(walletFullResponse.id)
        this.walletRedisTemplate.opsForValue().set(walletKey, walletFullResponse)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun update(request: WalletRequest) {
        if (request.id == null) throw IllegalArgumentException("Wallet Request id is null")
        val wallet = this.walletMapper.walletRepository.save(this.walletMapper.toEntity(request))
        this.handleCache(wallet)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun delete(id: UUID) {
        val findEntity = this.findEntity(id)
        this.walletMapper.walletRepository.delete(findEntity)
        this.handleCache(findEntity)
    }
}