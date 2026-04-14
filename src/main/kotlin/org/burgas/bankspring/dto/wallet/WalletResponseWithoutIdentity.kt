package org.burgas.bankspring.dto.wallet

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.account.AccountResponseWithoutWallet
import java.util.UUID

data class WalletResponseWithoutIdentity(
    override val id: UUID?,
    val accounts: List<AccountResponseWithoutWallet>?,
    val createdAt: String?
) : Response
