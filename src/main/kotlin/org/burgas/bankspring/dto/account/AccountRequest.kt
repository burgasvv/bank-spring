package org.burgas.bankspring.dto.account

import org.burgas.bankspring.dto.Request
import java.util.UUID

data class AccountRequest(
    override val id: UUID?,
    val walletId: UUID?
) : Request
