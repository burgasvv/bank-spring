package org.burgas.bankspring.dto.wallet

import org.burgas.bankspring.dto.Request
import java.util.UUID

data class WalletRequest(
    override val id: UUID?,
    val identityId: UUID?
) : Request