package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.transfer.Transfer
import org.burgas.bankspring.dto.transfer.TransferRequest
import org.burgas.bankspring.dto.transfer.TransferResponse
import org.burgas.bankspring.mapper.contract.SimpleMapper
import org.burgas.bankspring.repository.TransferRepository
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Optional
import java.util.UUID

@Component
class TransferMapper : SimpleMapper<TransferRequest, Transfer, TransferResponse> {

    final val transferRepository: TransferRepository

    private final val cardMapperObjectFactory: ObjectFactory<CardMapper>

    constructor(transferRepository: TransferRepository, cardMapperObjectFactory: ObjectFactory<CardMapper>) {
        this.transferRepository = transferRepository
        this.cardMapperObjectFactory = cardMapperObjectFactory
    }

    private fun getCardMapper(): CardMapper = this.cardMapperObjectFactory.`object`

    override fun toEntity(request: TransferRequest): Transfer {
        return Transfer().apply {
            val sender = getCardMapper().cardRepository
                .findById(request.senderId ?: UUID(0, 0))
                .orElse(null)
            this.sender = sender ?: throw IllegalArgumentException("Sender not found")
            val receiver = getCardMapper().cardRepository
                .findById(request.receiverId ?: UUID(0, 0))
                .orElse(null)
            this.receiver = receiver ?: throw IllegalArgumentException("Receiver not found")
            this.amount = request.amount ?: throw IllegalArgumentException("Amount is null")
            this.createdAt = LocalDateTime.now()
        }
    }

    override fun toResponse(entity: Transfer): TransferResponse {
        return TransferResponse(
            id = entity.id,
            sender = Optional.ofNullable(entity.sender)
                .map { this.getCardMapper().toShortResponse(it) }
                .orElse(null),
            receiver = Optional.ofNullable(entity.receiver)
                .map { this.getCardMapper().toShortResponse(it) }
                .orElse(null),
            amount = entity.amount,
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm"))
        )
    }
}