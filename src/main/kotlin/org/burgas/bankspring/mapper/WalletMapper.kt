package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.wallet.Wallet
import org.burgas.bankspring.dto.wallet.WalletFullResponse
import org.burgas.bankspring.dto.wallet.WalletRequest
import org.burgas.bankspring.dto.wallet.WalletResponseWithoutIdentity
import org.burgas.bankspring.dto.wallet.WalletShortResponse
import org.burgas.bankspring.mapper.contract.FullMapper
import org.burgas.bankspring.repository.WalletRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class WalletMapper : FullMapper<WalletRequest, Wallet, WalletShortResponse, WalletFullResponse> {

    final val walletRepository: WalletRepository

    private final val identityMapperObjectFactory: ObjectFactory<IdentityMapper>
    private final val accountMapperObjectFactory: ObjectFactory<AccountMapper>

    constructor(
        walletRepository: WalletRepository,
        identityMapperObjectFactory: ObjectFactory<IdentityMapper>,
        accountMapperObjectFactory: ObjectFactory<AccountMapper>
    ) {
        this.walletRepository = walletRepository
        this.identityMapperObjectFactory = identityMapperObjectFactory
        this.accountMapperObjectFactory = accountMapperObjectFactory
    }

    private fun getIdentityMapper(): IdentityMapper = this.identityMapperObjectFactory.`object`

    private fun getAccountMapper(): AccountMapper = this.accountMapperObjectFactory.`object`

    override fun toEntity(request: WalletRequest): Wallet {
        return this.walletRepository.findById(request.id ?: UUID(0,0))
            .map {
                Wallet().apply {
                    this.id = it.id
                    val findIdentity = getIdentityMapper().identityRepository
                        .findById(request.identityId ?: UUID(0, 0))
                        .orElse(null)
                    this.identity = findIdentity ?: it.identity
                    this.createdAt = it.createdAt
                }
            }
            .orElseGet {
                Wallet().apply {
                    val findIdentity = getIdentityMapper().identityRepository
                        .findById(request.identityId ?: UUID(0, 0))
                        .orElse(null)
                    this.identity = findIdentity ?: throw IllegalArgumentException("Identity not found for wallet")
                    this.createdAt = LocalDateTime.now()
                }
            }
    }

    override fun toShortResponse(entity: Wallet): WalletShortResponse {
        return WalletShortResponse(
            id = entity.id,
            identity = Optional.ofNullable(entity.identity)
                .map { this.getIdentityMapper().toShortResponse(it) }
                .orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    override fun toFullResponse(entity: Wallet): WalletFullResponse {
        return WalletFullResponse(
            id = entity.id,
            identity = Optional.ofNullable(entity.identity)
                .map { this.getIdentityMapper().toShortResponse(it) }
                .orElse(null),
            accounts = entity.accounts.map { this.getAccountMapper().toAccountResponseWithoutWallet(it) },
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    fun toWalletResponseWithoutIdentity(entity: Wallet): WalletResponseWithoutIdentity {
        return WalletResponseWithoutIdentity(
            id = entity.id,
            accounts = entity.accounts.map { this.getAccountMapper().toAccountResponseWithoutWallet(it) },
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }
}