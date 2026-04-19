package org.burgas.bankspring.dto.account

import org.burgas.bankspring.dto.Response
import java.util.*

data class AccountResponseWithoutWalletAndCard(
    override val id: UUID?,
    val number: Long?,
    val inn: Long?,
    val cpp: Long?,
    val createdAt: String?
) : Response
