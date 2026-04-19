package org.burgas.bankspring.dto.operation

import org.burgas.bankspring.dao.operation.OperationType
import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.card.CardResponseWithoutAccount
import org.burgas.bankspring.dto.card.CardShortResponse
import java.util.UUID

data class OperationFullResponse(
    override val id: UUID?,
    val type: OperationType?,
    val amount: Double?,
    val card: CardResponseWithoutAccount?,
    val createdAt: String?
) : Response
