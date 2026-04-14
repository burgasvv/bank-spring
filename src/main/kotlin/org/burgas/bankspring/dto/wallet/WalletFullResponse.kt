package org.burgas.bankspring.dto.wallet

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.account.AccountResponseWithoutWallet
import org.burgas.bankspring.dto.identity.IdentityShortResponse
import java.util.UUID

data class WalletFullResponse(
    override val id: UUID?,
    val identity: IdentityShortResponse?,
    val accounts: List<AccountResponseWithoutWallet>?,
    val createdAt: String?
) : Response
