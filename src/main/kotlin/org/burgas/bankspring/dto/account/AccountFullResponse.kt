package org.burgas.bankspring.dto.account

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.card.CardResponseWithoutAccount
import org.burgas.bankspring.dto.wallet.WalletShortResponse
import java.util.UUID

data class AccountFullResponse(
    override val id: UUID?,
    val number: Long?,
    val inn: Long?,
    val cpp: Long?,
    val wallet: WalletShortResponse?,
    val card: CardResponseWithoutAccount?,
    val createdAt: String?
) : Response
