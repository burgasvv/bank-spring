package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.identity.Authority
import org.burgas.bankspring.dao.identity.Identity
import org.burgas.bankspring.dto.identity.IdentityFullResponse
import org.burgas.bankspring.dto.identity.IdentityRequest
import org.burgas.bankspring.dto.identity.IdentityShortResponse
import org.burgas.bankspring.mapper.contract.FullMapper
import org.burgas.bankspring.repository.IdentityRepository
import org.burgas.bankspring.util.RegularUtil
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class IdentityMapper : FullMapper<IdentityRequest, Identity, IdentityShortResponse, IdentityFullResponse> {

    final val identityRepository: IdentityRepository

    private final val walletMapperObjectFactory: ObjectFactory<WalletMapper>

    constructor(identityRepository: IdentityRepository, walletMapperObjectMapper: ObjectFactory<WalletMapper>) {
        this.identityRepository = identityRepository
        this.walletMapperObjectFactory = walletMapperObjectMapper
    }

    private fun getWalletMapper(): WalletMapper = this.walletMapperObjectFactory.`object`

    override fun toEntity(request: IdentityRequest): Identity {
        return this.identityRepository.findById(request.id ?: UUID(0, 0))
            .map {
                Identity().apply {
                    this.id = it.id
                    this.authority = request.authority ?: it.authority
                    this.email = request.email ?: it.email
                    this.password = it.password
                    if (request.phone != null) {
                        this.phone = if (RegularUtil.PHONE_REGEX.matches(request.phone)) request.phone
                        else throw IllegalArgumentException("Phone regex not matched")
                    } else {
                        this.phone = it.phone
                    }
                    this.status = it.status
                    this.firstname = request.firstname ?: it.firstname
                    this.lastname = request.lastname ?: it.lastname
                    this.patronymic = request.patronymic ?: it.patronymic
                }
            }
            .orElseGet {
                Identity().apply {
                    this.authority = request.authority ?: Authority.USER
                    this.email = request.email ?: throw IllegalArgumentException("Email is null")
                    this.password = request.password ?: throw IllegalArgumentException("Password is null")
                    if (request.phone != null) {
                        this.phone = if (RegularUtil.PHONE_REGEX.matches(request.phone)) request.phone
                        else throw IllegalArgumentException("Phone regex not matched")
                    } else {
                        throw IllegalArgumentException("Phone is null")
                    }
                    this.status = request.status ?: true
                    this.firstname = request.firstname ?: throw IllegalArgumentException("Firstname is null")
                    this.lastname = request.lastname ?: throw IllegalArgumentException("Lastname is null")
                    this.patronymic = request.patronymic ?: throw IllegalArgumentException("Patronymic is null")
                }
            }
    }

    override fun toShortResponse(entity: Identity): IdentityShortResponse {
        return IdentityShortResponse(
            id = entity.id,
            email = entity.email,
            phone = entity.phone,
            firstname = entity.firstname,
            lastname = entity.lastname,
            patronymic = entity.patronymic
        )
    }

    override fun toFullResponse(entity: Identity): IdentityFullResponse {
        return IdentityFullResponse(
            id = entity.id,
            email = entity.email,
            phone = entity.phone,
            firstname = entity.firstname,
            lastname = entity.lastname,
            patronymic = entity.patronymic,
            wallet = Optional.ofNullable(entity.wallet)
                .map { this.getWalletMapper().toWalletResponseWithoutIdentity(it) }
                .orElse(null)
        )
    }
}