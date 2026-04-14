package org.burgas.bankspring.dto.operation

import org.burgas.bankspring.dao.operation.OperationType
import org.burgas.bankspring.dto.Response
import java.util.*

data class OperationShortResponse(
    override val id: UUID?,
    val type: OperationType?,
    val amount: Double?,
    val createdAt: String?
) : Response
