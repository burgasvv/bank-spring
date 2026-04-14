package org.burgas.bankspring.dto.identity

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.wallet.WalletResponseWithoutIdentity
import java.util.UUID

data class IdentityFullResponse(
    override val id: UUID?,
    val email: String?,
    val phone: String?,
    val firstname: String?,
    val lastname: String?,
    val patronymic: String?,
    val wallet: WalletResponseWithoutIdentity?
) : Response
