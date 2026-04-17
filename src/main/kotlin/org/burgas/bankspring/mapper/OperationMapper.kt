package org.burgas.bankspring.mapper

import org.burgas.bankspring.dao.operation.Operation
import org.burgas.bankspring.dto.operation.OperationFullResponse
import org.burgas.bankspring.dto.operation.OperationRequest
import org.burgas.bankspring.dto.operation.OperationShortResponse
import org.burgas.bankspring.mapper.contract.FullMapper
import org.burgas.bankspring.repository.OperationRepository
import org.burgas.bankspring.mapper.contract.IOperation
import org.springframework.beans.factory.ObjectFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Optional
import java.util.UUID

@Component
class OperationMapper : FullMapper<OperationRequest, Operation, OperationShortResponse, OperationFullResponse>,
    IOperation {

    final val operationRepository: OperationRepository

    private final val cardMapperObjectFactory: ObjectFactory<CardMapper>

    constructor(
        operationRepository: OperationRepository,
        cardMapperObjectFactory: ObjectFactory<CardMapper>
    ) {
        this.operationRepository = operationRepository
        this.cardMapperObjectFactory = cardMapperObjectFactory
    }

    private fun getCardMapper(): CardMapper = this.cardMapperObjectFactory.`object`

    override fun toEntity(request: OperationRequest): Operation {
        val operation = Operation().apply {
            val findCard = getCardMapper().cardRepository
                .findCardByIdWithPessimisticLock(request.cardId ?: UUID(0, 0))
                .orElse(null)
            this.type = request.type ?: throw IllegalArgumentException("Operation type is null")
            this.amount = request.amount ?: throw IllegalArgumentException("Amount is null")
            this.card = findCard ?: throw IllegalArgumentException("Card not found for operation")
            this.createdAt = LocalDateTime.now()
        }
        this.operate(operation)
        return operation
    }

    override fun toShortResponse(entity: Operation): OperationShortResponse {
        return OperationShortResponse(
            id = entity.id,
            type = entity.type,
            amount = entity.amount,
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, h:mm"))
        )
    }

    override fun toFullResponse(entity: Operation): OperationFullResponse {
        return OperationFullResponse(
            id = entity.id,
            type = entity.type,
            amount = entity.amount,
            card = Optional.ofNullable(entity.card)
                .map { this.getCardMapper().toShortResponse(it) }
                .orElse(null),
            createdAt = entity.createdAt.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, h:mm"))
        )
    }
}