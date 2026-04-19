package org.burgas.bankspring.dto.transfer

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.card.CardResponseWithoutAccount
import org.burgas.bankspring.dto.card.CardShortResponse
import java.util.UUID

data class TransferResponse(
    override val id: UUID?,
    val sender: CardResponseWithoutAccount?,
    val receiver: CardResponseWithoutAccount?,
    val amount: Double?,
    val createdAt: String?
) : Response
