package org.burgas.bankspring.dto.operation

import org.burgas.bankspring.dao.operation.OperationType
import org.burgas.bankspring.dto.Request
import java.util.UUID

data class OperationRequest(
    override val id: UUID?,
    val type: OperationType,
    val amount: Double,
    val cardId: UUID
) : Request
