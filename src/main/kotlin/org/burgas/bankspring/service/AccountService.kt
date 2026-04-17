package org.burgas.bankspring.service

import org.burgas.bankspring.dao.account.Account
import org.burgas.bankspring.dto.account.AccountFullResponse
import org.burgas.bankspring.dto.account.AccountRequest
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.mapper.AccountMapper
import org.burgas.bankspring.redis.RedisCacheHandler
import org.burgas.bankspring.service.contract.CrudService
import org.burgas.bankspring.util.KeyUtil
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class AccountService : org.burgas.bankspring.service.contract.Service,
    CrudService<AccountRequest, Account, AccountFullResponse>, RedisCacheHandler<Account> {

    private final val accountMapper: AccountMapper

    @Qualifier(value = "identityRedisTemplate")
    private final val identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>

    @Qualifier(value = "walletRedisTemplate")
    private final val walletRedisTemplate: RedisTemplate<String, WalletFullResponse>

    @Qualifier(value = "accountRedisTemplate")
    private final val accountRedisTemplate: RedisTemplate<String, AccountFullResponse>

    @Qualifier(value = "cardRedisTemplate")
    private final val cardRedisTemplate: RedisTemplate<String, CardFullResponse>

    constructor(
        accountMapper: AccountMapper,
        identityRedisTemplate: RedisTemplate<String, IdentityFullResponse>,
        walletRedisTemplate: RedisTemplate<String, WalletFullResponse>,
        accountRedisTemplate: RedisTemplate<String, AccountFullResponse>,
        cardRedisTemplate: RedisTemplate<String, CardFullResponse>
    ) {
        this.accountMapper = accountMapper
        this.identityRedisTemplate = identityRedisTemplate
        this.walletRedisTemplate = walletRedisTemplate
        this.accountRedisTemplate = accountRedisTemplate
        this.cardRedisTemplate = cardRedisTemplate
    }

    override fun handleCache(entity: Account) {
        val accountKey = KeyUtil.ACCOUNT_KEY.format(entity.id)
        if (this.accountRedisTemplate.hasKey(accountKey)) this.accountRedisTemplate.delete(accountKey)

        val wallet = entity.wallet
        if (wallet != null) {
            val walletKey = KeyUtil.WALLET_KEY.format(wallet.id)
            if (this.walletRedisTemplate.hasKey(walletKey)) this.walletRedisTemplate.delete(walletKey)

            val identity = wallet.identity
            if (identity != null) {
                val identityKey = KeyUtil.IDENTITY_KEY.format(identity.id)
                if (this.identityRedisTemplate.hasKey(identityKey)) this.identityRedisTemplate.delete(identityKey)
            }
        }

        val card = entity.card
        if (card != null) {
            val cardKey = KeyUtil.CARD_KEY.format(card.id)
            if (this.cardRedisTemplate.hasKey(cardKey)) this.cardRedisTemplate.delete(cardKey)
        }
    }

    override fun findEntity(id: UUID): Account {
        return this.accountMapper.accountRepository.findById(id)
            .orElseThrow { throw IllegalArgumentException("Account not found") }
    }

    override fun findById(id: UUID): AccountFullResponse {
        val accountKey = KeyUtil.ACCOUNT_KEY.format(id)
        val accountFromRedis = this.accountRedisTemplate.opsForValue().get(accountKey)
        if (accountFromRedis != null) {
            return accountFromRedis
        } else {
            val accountFullResponse = this.accountMapper.toFullResponse(this.findEntity(id))
            this.accountRedisTemplate.opsForValue().set(accountKey, accountFullResponse)
            return accountFullResponse
        }
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun create(request: AccountRequest) {
        val account = this.accountMapper.accountRepository.save(this.accountMapper.toEntity(request))
        this.handleCache(account)
        val accountFullResponse = this.accountMapper.toFullResponse(account)
        val accountKey = KeyUtil.ACCOUNT_KEY.format(accountFullResponse.id)
        this.accountRedisTemplate.opsForValue().set(accountKey, accountFullResponse)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun update(request: AccountRequest) {
        if (request.id == null) throw IllegalArgumentException("Account Request id is null")
        val account = this.accountMapper.accountRepository.save(this.accountMapper.toEntity(request))
        this.handleCache(account)
    }

    @Transactional(
        isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED,
        rollbackFor = [Throwable::class, Exception::class, RuntimeException::class]
    )
    override fun delete(id: UUID) {
        val findEntity = this.findEntity(id)
        this.accountMapper.accountRepository.delete(findEntity)
        this.handleCache(findEntity)
    }
}