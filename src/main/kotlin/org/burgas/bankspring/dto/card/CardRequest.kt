package org.burgas.bankspring.dto.card

import org.burgas.bankspring.dto.Request
import java.time.LocalDate
import java.util.UUID

data class CardRequest(
    override val id: UUID? = null,
    val validUntil: LocalDate? = null,
    val pin: String? = null,
    val accountId: UUID? = null
) : Request
