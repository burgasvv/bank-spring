package org.burgas.bankspring.service

import org.burgas.bankspring.dao.identity.Identity
import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.identity.IdentityRequest
import org.burgas.bankspring.dto.identity.IdentityShortResponse
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.mapper.IdentityMapper
import org.burgas.bankspring.redis.RedisCacheHandler
import org.burgas.bankspring.service.contract.CrudService
import org.burgas.bankspring.service.contract.ListService
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
class IdentityService : org.burgas.bankspring.service.contract.Service,
    CrudService<IdentityRequest, Identity, IdentityFullResponse>, ListService<IdentityShortResponse>,
    RedisCacheHandler<Identity> {

    private final val identityMapper: IdentityMapper

    @Qualifier(value = "identityRedisTemplate")
    private final val identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>

    @Qualifier(value = "walletRedisTemplate")
    private final val walletRedisTemplate: RedisTemplate<String, WalletFullResponse>

    @Qualifier(value = "accountRedisTemplate")
    private final val accountRedisTemplate: RedisTemplate<String, AccountFullResponse>

    @Qualifier(value = "cardRedisTemplate")
    private final val cardRedisTemplate: RedisTemplate<String, CardFullResponse>

    constructor(
        identityMapper: IdentityMapper,
        identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>,
        walletRedisTemplate: RedisTemplate<String, WalletFullResponse>,
        accountRedisTemplate: RedisTemplate<String, AccountFullResponse>,
        cardRedisTemplate: RedisTemplate<String, CardFullResponse>
    ) {
        this.identityMapper = identityMapper
        this.identityRedisTemplate = identityRedisTemplate
        this.walletRedisTemplate = walletRedisTemplate
        this.accountRedisTemplate = accountRedisTemplate
        this.cardRedisTemplate = cardRedisTemplate
    }

    override fun handleCache(entity: Identity) {
        val identityKey = KeyUtil.IDENTITY_KEY.format(entity.id)
        if (this.identityRedisTemplate.hasKey(identityKey)) this.identityRedisTemplate.delete(identityKey)

        val wallet = entity.wallet
        if (wallet != null) {
            val walletKey = KeyUtil.WALLET_KEY.format(wallet.id)
            if (this.walletRedisTemplate.hasKey(walletKey)) this.walletRedisTemplate.delete(walletKey)

            val accounts = wallet.accounts
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
    }

    override fun findEntity(id: UUID): Identity {
        return this.identityMapper.identityRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException("Identity not found") }
    }

    override fun findAll(): List<IdentityShortResponse> {
        return this.identityMapper.identityRepository.findAll()
            .map { this.identityMapper.toShortResponse(it) }
    }

    override fun findById(id: UUID): IdentityFullResponse {
        val identityKey = KeyUtil.IDENTITY_KEY.format(id)
        val identityFromRedis = this.identityRedisTemplate.opsForValue().get(identityKey)
        if (identityFromRedis != null) {
            return identityFromRedis
        } else {
            val identityFullResponse = this.identityMapper.toFullResponse(this.findEntity(id))
            this.identityRedisTemplate.opsForValue().set(identityKey, identityFullResponse)
            return identityFullResponse
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun create(request: IdentityRequest) {
        val identityFullResponse = this.identityMapper.toFullResponse(
            this.identityMapper.identityRepository.save(this.identityMapper.toEntity(request))
        )
        val identityKey = KeyUtil.IDENTITY_KEY.format(identityFullResponse.id)
        this.identityRedisTemplate.opsForValue().set(identityKey, identityFullResponse)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun update(request: IdentityRequest) {
        if (request.id == null) throw IllegalArgumentException("Identity Request id is null")
        val identity = this.identityMapper.identityRepository.save(this.identityMapper.toEntity(request))
        this.handleCache(identity)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun delete(id: UUID) {
        val findEntity = this.findEntity(id)
        this.identityMapper.identityRepository.delete(findEntity)
        this.handleCache(findEntity)
    }
}