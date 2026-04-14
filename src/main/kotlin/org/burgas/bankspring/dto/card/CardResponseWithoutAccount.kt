package org.burgas.bankspring.dto.card

import org.burgas.bankspring.dto.Response
import java.util.UUID

data class CardResponseWithoutAccount(
    override val id: UUID?,
    val number: String?,
    val code: Long?,
    val validUntil: String?,
    val balance: Double?,
    val createdAt: String?
) : Response
