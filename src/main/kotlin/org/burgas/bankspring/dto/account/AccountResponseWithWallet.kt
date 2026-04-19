package org.burgas.bankspring.dto.account

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.wallet.WalletShortResponse
import java.util.*

data class AccountResponseWithWallet(
    override val id: UUID?,
    val number: Long?,
    val inn: Long?,
    val cpp: Long?,
    val wallet: WalletShortResponse?,
    val createdAt: String?
) : Response
