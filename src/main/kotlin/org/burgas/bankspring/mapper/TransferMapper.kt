package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.transfer.Transfer
import org.burgas.bankspring.dto.transfer.TransferRequest
import org.burgas.bankspring.dto.transfer.TransferResponse
import org.burgas.bankspring.mapper.contract.ITransfer
import org.burgas.bankspring.mapper.contract.SimpleMapper
import org.burgas.bankspring.repository.TransferRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class TransferMapper : SimpleMapper<TransferRequest, Transfer, TransferResponse>, ITransfer {

    final val transferRepository: TransferRepository

    private final val cardMapperObjectFactory: ObjectFactory<CardMapper>

    constructor(transferRepository: TransferRepository, cardMapperObjectFactory: ObjectFactory<CardMapper>) {
        this.transferRepository = transferRepository
        this.cardMapperObjectFactory = cardMapperObjectFactory
    }

    private fun getCardMapper(): CardMapper = this.cardMapperObjectFactory.`object`

    override fun toEntity(request: TransferRequest): Transfer {
        val transfer = Transfer().apply {
            val sender = getCardMapper().cardRepository
                .findCardByIdWithPessimisticLock(request.senderId)
                .orElse(null)
            val receiver = getCardMapper().cardRepository
                .findCardByIdWithPessimisticLock(request.receiverId)
                .orElse(null)
            this.sender = sender ?: throw IllegalArgumentException("Sender not found")
            this.receiver = receiver ?: throw IllegalArgumentException("Receiver not found")
            this.amount = request.amount
            this.createdAt = LocalDateTime.now()
        }
        this.transfer(transfer)
        return transfer
    }

    override fun toResponse(entity: Transfer): TransferResponse {
        return TransferResponse(
            id = entity.id,
            sender = Optional.ofNullable(entity.sender)
                .map { this.getCardMapper().toCardResponseWithoutAccount(it) }
                .orElse(null),
            receiver = Optional.ofNullable(entity.receiver)
                .map { this.getCardMapper().toCardResponseWithoutAccount(it) }
                .orElse(null),
            amount = entity.amount,
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }
}