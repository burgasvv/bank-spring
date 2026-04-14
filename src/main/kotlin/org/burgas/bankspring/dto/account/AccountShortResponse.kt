package org.burgas.bankspring.dto.account

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.wallet.WalletResponseWithoutIdentity
import java.util.*

data class AccountShortResponse(
    override val id: UUID?,
    val number: Long?,
    val inn: Long?,
    val cpp: Long?,
    val wallet: WalletResponseWithoutIdentity?,
    val createdAt: String?
) : Response
