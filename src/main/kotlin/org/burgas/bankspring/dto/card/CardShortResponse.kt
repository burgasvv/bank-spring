package org.burgas.bankspring.dto.card

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.account.AccountShortResponse
import java.util.UUID

data class CardShortResponse(
    override val id: UUID?,
    val number: String?,
    val code: Long?,
    val validUntil: String?,
    val balance: Double?,
    val account: AccountShortResponse?,
    val createdAt: String?
) : Response
