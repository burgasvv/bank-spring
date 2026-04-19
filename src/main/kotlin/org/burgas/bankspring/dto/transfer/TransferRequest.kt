package org.burgas.bankspring.dto.transfer

import org.burgas.bankspring.dto.Request
import java.util.UUID

data class TransferRequest(
    override val id: UUID?,
    val senderId: UUID,
    val receiverId: UUID,
    val amount: Double
) : Request
