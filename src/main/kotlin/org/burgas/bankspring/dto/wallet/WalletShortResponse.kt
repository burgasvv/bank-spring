package org.burgas.bankspring.dto.wallet

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.identity.IdentityShortResponse
import java.util.UUID

data class WalletShortResponse(
    override val id: UUID?,
    val identity: IdentityShortResponse?,
    val createdAt: String?
) : Response
