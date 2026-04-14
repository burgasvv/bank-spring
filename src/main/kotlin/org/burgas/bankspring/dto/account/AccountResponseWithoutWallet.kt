package org.burgas.bankspring.dto.account

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.card.CardResponseWithoutAccount
import java.util.*

data class AccountResponseWithoutWallet(
    override val id: UUID?,
    val number: Long?,
    val inn: Long?,
    val cpp: Long?,
    val card: CardResponseWithoutAccount?,
    val createdAt: String?
) : Response
