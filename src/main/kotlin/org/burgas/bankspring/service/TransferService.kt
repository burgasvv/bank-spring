package org.burgas.bankspring.service

import org.burgas.bankspring.dao.transfer.Transfer
import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.transfer.TransferRequest
import org.burgas.bankspring.dto.transfer.TransferResponse
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.mapper.TransferMapper
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
class TransferService : org.burgas.bankspring.service.contract.Service,
    SimpleCrudService<TransferRequest, Transfer, TransferResponse>, RedisCacheHandler<Transfer> {

    private final val transferMapper: TransferMapper

    @Qualifier(value = "identityRedisTemplate")
    private final val identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>

    @Qualifier(value = "walletRedisTemplate")
    private final val walletRedisTemplate: RedisTemplate<String, WalletFullResponse>

    @Qualifier(value = "accountRedisTemplate")
    private final val accountRedisTemplate: RedisTemplate<String, AccountFullResponse>

    @Qualifier(value = "cardRedisTemplate")
    private final val cardRedisTemplate: RedisTemplate<String, CardFullResponse>

    constructor(
        transferMapper: TransferMapper,
        identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>,
        walletRedisTemplate: RedisTemplate<String, WalletFullResponse>,
        accountRedisTemplate: RedisTemplate<String, AccountFullResponse>,
        cardRedisTemplate: RedisTemplate<String, CardFullResponse>
    ) {
        this.transferMapper = transferMapper
        this.identityRedisTemplate = identityRedisTemplate
        this.walletRedisTemplate = walletRedisTemplate
        this.accountRedisTemplate = accountRedisTemplate
        this.cardRedisTemplate = cardRedisTemplate
    }

    override fun handleCache(entity: Transfer) {
        val sender = entity.sender
        if (sender != null) {
            val cardKey = KeyUtil.CARD_KEY.format(sender.id)
            if (this.cardRedisTemplate.hasKey(cardKey)) this.cardRedisTemplate.delete(cardKey)

            val account = sender.account
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

        val receiver = entity.receiver
        if (receiver != null) {
            val cardKey = KeyUtil.CARD_KEY.format(receiver.id)
            if (this.cardRedisTemplate.hasKey(cardKey)) this.cardRedisTemplate.delete(cardKey)

            val account = receiver.account
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

    override fun findEntity(id: UUID): Transfer {
        return this.transferMapper.transferRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException("Transfer not found") }
    }

    override fun findById(id: UUID): TransferResponse {
        return this.transferMapper.toResponse(this.findEntity(id))
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Exception::class, Throwable::class, RuntimeException::class]
    )
    override fun create(request: TransferRequest) {
        val transfer = this.transferMapper.transferRepository.save(this.transferMapper.toEntity(request))
        this.handleCache(transfer)
    }
}