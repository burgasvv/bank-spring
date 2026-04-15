package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.card.Card
import org.burgas.bankspring.dto.card.CardFullResponse
import org.burgas.bankspring.dto.card.CardRequest
import org.burgas.bankspring.dto.card.CardResponseWithoutAccount
import org.burgas.bankspring.dto.card.CardShortResponse
import org.burgas.bankspring.mapper.contract.FullMapper
import org.burgas.bankspring.repository.CardRepository
import org.burgas.bankspring.util.RegularUtil
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Optional
import java.util.UUID
import java.util.random.RandomGenerator

@Component
class CardMapper : FullMapper<CardRequest, Card, CardShortResponse, CardFullResponse> {

    final val cardRepository: CardRepository

    private final val accountMapperObjectFactory: ObjectFactory<AccountMapper>
    private final val operationMapperObjectFactory: ObjectFactory<OperationMapper>
    private final val transferMapperObjectFactory: ObjectFactory<TransferMapper>

    constructor(
        cardRepository: CardRepository,
        accountMapperObjectFactory: ObjectFactory<AccountMapper>,
        operationMapperObjectFactory: ObjectFactory<OperationMapper>,
        transferMapperObjectFactory: ObjectFactory<TransferMapper>
    ) {
        this.cardRepository = cardRepository
        this.accountMapperObjectFactory = accountMapperObjectFactory
        this.operationMapperObjectFactory = operationMapperObjectFactory
        this.transferMapperObjectFactory = transferMapperObjectFactory
    }

    private fun getAccountMapper(): AccountMapper = this.accountMapperObjectFactory.`object`

    private fun getOperationMapper(): OperationMapper = this.operationMapperObjectFactory.`object`

    private fun getTransferMapper(): TransferMapper = this.transferMapperObjectFactory.`object`

    override fun toEntity(request: CardRequest): Card {
        return this.cardRepository.findById(request.id ?: UUID(0,0))
            .map {
                Card().apply {
                    this.id = it.id
                    this.number = it.number
                    this.code = it.code
                    this.validUntil = request.validUntil ?: it.validUntil
                    this.balance = it.balance
                    if (request.pin != null) {
                        this.pin = if (RegularUtil.PIN_REGEX.matches(request.pin)) request.pin
                        else throw IllegalArgumentException("Pin regex not matched")
                    } else {
                        this.pin = it.pin
                    }
                    val findAccount = getAccountMapper().accountRepository
                        .findById(request.accountId ?: UUID(0, 0))
                        .orElse(null)
                    this.account = findAccount ?: it.account
                    this.createdAt = it.createdAt
                }
            }
            .orElseGet {
                Card().apply {
                    val longMin = 1000_0000_0000_0000
                    val longMax = 9999_9999_9999_9999
                    var createNumber = RandomGenerator.getDefault().nextLong(longMin, longMax)
                        .toString().chunked(4).joinToString(" ")

                    while (cardRepository.existsCardByNumber(createNumber)) {
                        createNumber = RandomGenerator.getDefault().nextLong(longMin, longMax)
                            .toString().chunked(4).joinToString(" ")
                    }
                    this.number = createNumber
                    this.code = RandomGenerator.getDefault().nextLong(100, 999)

                    if (request.pin != null) {
                        this.pin = if (RegularUtil.PIN_REGEX.matches(request.pin)) request.pin
                        else throw IllegalArgumentException("Pin regex not matched")
                    } else {
                        throw IllegalArgumentException("Pin is null")
                    }

                    this.balance = 0.0
                    val findAccount = getAccountMapper().accountRepository
                        .findById(request.accountId ?: UUID(0, 0))
                        .orElse(null)
                    this.account = findAccount ?: throw IllegalArgumentException("Account not found for card")
                    this.createdAt = LocalDateTime.now()
                }
            }
    }

    override fun toShortResponse(entity: Card): CardShortResponse {
        return CardShortResponse(
            id = entity.id,
            number = entity.number,
            code = entity.code,
            validUntil = entity.validUntil.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            balance = entity.balance,
            account = Optional.ofNullable(entity.account)
                .map { this.getAccountMapper().toShortResponse(it) }
                .orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    override fun toFullResponse(entity: Card): CardFullResponse {
        return CardFullResponse(
            id = entity.id,
            number = entity.number,
            code = entity.code,
            validUntil = entity.validUntil.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            balance = entity.balance,
            account = Optional.ofNullable(entity.account)
                .map { this.getAccountMapper().toShortResponse(it) }
                .orElse(null),
            operations = entity.operations.map { this.getOperationMapper().toShortResponse(it) },
            transfersBySender = entity.transfersBySender.map { this.getTransferMapper().toResponse(it) },
            transfersByReceiver = entity.transfersByReceiver.map { this.getTransferMapper().toResponse(it) },
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }

    fun toCardResponseWithoutAccount(entity: Card): CardResponseWithoutAccount {
        return CardResponseWithoutAccount(
            id = entity.id,
            number = entity.number,
            code = entity.code,
            validUntil = entity.validUntil.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            balance = entity.balance,
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }
}