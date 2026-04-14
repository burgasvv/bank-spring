package org.burgas.bankspring.dto.card

import org.burgas.bankspring.dto.Request
import java.time.LocalDate
import java.util.UUID

data class CardRequest(
    override val id: UUID?,
    val validUntil: LocalDate?,
    val pin: String?,
    val accountId: UUID?
) : Request
