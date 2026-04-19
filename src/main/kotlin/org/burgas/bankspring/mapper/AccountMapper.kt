package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.account.Account
import org.burgas.bankspring.dto.account.*
import org.burgas.bankspring.mapper.contract.FullMapper
import org.burgas.bankspring.repository.AccountRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.random.RandomGenerator

@Component
class AccountMapper : FullMapper<AccountRequest, Account, AccountShortResponse, AccountFullResponse> {

    final val accountRepository: AccountRepository

    private final val walletMapperObjectFactory: ObjectFactory<WalletMapper>
    private final val cardMapperObjectFactory: ObjectFactory<CardMapper>

    constructor(
        accountRepository: AccountRepository,
        walletMapperObjectFactory: ObjectFactory<WalletMapper>,
        cardMapperObjectFactory: ObjectFactory<CardMapper>
    ) {
        this.accountRepository = accountRepository
        this.walletMapperObjectFactory = walletMapperObjectFactory
        this.cardMapperObjectFactory = cardMapperObjectFactory
    }

    private fun getWalletMapper(): WalletMapper = this.walletMapperObjectFactory.`object`

    private fun getCardMapper(): CardMapper = this.cardMapperObjectFactory.`object`

    override fun toEntity(request: AccountRequest): Account {
        return this.accountRepository.findById(request.id ?: UUID(0, 0))
            .map {
                Account().apply {
                    this.id = it.id
                    this.number = it.number
                    this.inn = it.inn
                    this.cpp = it.cpp
                    this.createdAt = it.createdAt
                    val findWallet = getWalletMapper().walletRepository
                        .findById(request.walletId ?: UUID(0, 0))
                        .orElse(null)
                    this.wallet = findWallet ?: it.wallet
                }
            }
            .orElseGet {
                Account().apply {
                    var createNumber: Long = RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE)
                    var createInn: Long = RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE)
                    var createCpp: Long = RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE)

                    while (accountRepository.existsAccountByNumber(createNumber))
                        createNumber = RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE)

                    while (accountRepository.existsAccountByInn(createInn))
                        createInn = RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE)

                    while (accountRepository.existsAccountByCpp(createCpp))
                        createCpp = RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE)

                    this.number = createNumber
                    this.inn = createInn
                    this.cpp = createCpp
                    this.createdAt = LocalDateTime.now()
                    val findWallet = getWalletMapper().walletRepository
                        .findById(request.walletId ?: UUID(0, 0))
                        .orElse(null)
                    this.wallet = findWallet ?: throw IllegalArgumentException("Wallet not found for account")
                }
            }
    }

    override fun toShortResponse(entity: Account): AccountShortResponse {
        return AccountShortResponse(
            id = entity.id,
            number = entity.number,
            inn = entity.inn,
            cpp = entity.cpp,
            wallet = Optional.ofNullable(entity.wallet)
                .map { this.getWalletMapper().toWalletResponseWithoutIdentity(it) }.orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    override fun toFullResponse(entity: Account): AccountFullResponse {
        return AccountFullResponse(
            id = entity.id,
            number = entity.number,
            inn = entity.inn,
            cpp = entity.cpp,
            card = Optional.ofNullable(entity.card)
                .map { this.getCardMapper().toCardResponseWithoutAccount(it) }
                .orElse(null),
            wallet = Optional.ofNullable(entity.wallet)
                .map { this.getWalletMapper().toShortResponse(it) }
                .orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    fun toAccountResponseWithoutWallet(entity: Account): AccountResponseWithoutWallet {
        return AccountResponseWithoutWallet(
            id = entity.id,
            number = entity.number,
            inn = entity.inn,
            cpp = entity.cpp,
            card = Optional.ofNullable(entity.card)
                .map { this.getCardMapper().toCardResponseWithoutAccount(it) }
                .orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    fun toAccountResponseWithWallet(entity: Account): AccountResponseWithWallet {
        return AccountResponseWithWallet(
            id = entity.id,
            number = entity.number,
            inn = entity.inn,
            cpp = entity.cpp,
            wallet = Optional.ofNullable(entity.wallet)
                .map { this.getWalletMapper().toShortResponse(it) }
                .orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }
}