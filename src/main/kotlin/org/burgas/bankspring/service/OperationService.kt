package org.burgas.bankspring.service

import org.burgas.bankspring.dao.operation.Operation
import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.operation.OperationFullResponse
import org.burgas.bankspring.dto.operation.OperationRequest
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.mapper.OperationMapper
import org.burgas.bankspring.redis.RedisCacheHandler
import org.burgas.bankspring.service.contract.SimpleCrudService
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
class OperationService : org.burgas.bankspring.service.contract.Service,
    SimpleCrudService<OperationRequest, Operation, OperationFullResponse>, RedisCacheHandler<Operation> {

    private final val operationMapper: OperationMapper

    @Qualifier(value = "identityRedisTemplate")
    private final val identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>

    @Qualifier(value = "walletRedisTemplate")
    private final val walletRedisTemplate: RedisTemplate<String, WalletFullResponse>

    @Qualifier(value = "accountRedisTemplate")
    private final val accountRedisTemplate: RedisTemplate<String, AccountFullResponse>

    @Qualifier(value = "cardRedisTemplate")
    private final val cardRedisTemplate: RedisTemplate<String, CardFullResponse>

    constructor(
        operationMapper: OperationMapper,
        identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>,
        walletRedisTemplate: RedisTemplate<String, WalletFullResponse>,
        accountRedisTemplate: RedisTemplate<String, AccountFullResponse>,
        cardRedisTemplate: RedisTemplate<String, CardFullResponse>
    ) {
        this.operationMapper = operationMapper
        this.identityRedisTemplate = identityRedisTemplate
        this.walletRedisTemplate = walletRedisTemplate
        this.accountRedisTemplate = accountRedisTemplate
        this.cardRedisTemplate = cardRedisTemplate
    }

    override fun handleCache(entity: Operation) {
        val card = entity.card
        if (card != null) {
            val cardKey = KeyUtil.CARD_KEY.format(card.id)
            if (this.cardRedisTemplate.hasKey(cardKey)) this.cardRedisTemplate.delete(cardKey)

            val account = card.account
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
    }

    override fun findEntity(id: UUID): Operation {
        return this.operationMapper.operationRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException("Operation not found") }
    }

    override fun findById(id: UUID): OperationFullResponse {
        return this.operationMapper.toFullResponse(this.findEntity(id))
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun create(request: OperationRequest) {
        val operation = this.operationMapper.operationRepository.save(this.operationMapper.toEntity(request))
        this.handleCache(operation)
    }
}