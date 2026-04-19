package org.burgas.bankspring.dto.card

import org.burgas.bankspring.dto.Response
import org.burgas.bankspring.dto.account.AccountResponseWithWallet
import org.burgas.bankspring.dto.account.AccountResponseWithoutWalletAndCard
import org.burgas.bankspring.dto.account.AccountShortResponse
import org.burgas.bankspring.dto.operation.OperationShortResponse
import org.burgas.bankspring.dto.transfer.TransferResponse
import java.util.UUID

data class CardFullResponse(
    override val id: UUID?,
    val number: String?,
    val code: Long?,
    val validUntil: String?,
    val balance: Double?,
    val account: AccountResponseWithWallet?,
    val operations: List<OperationShortResponse>?,
    val transfersBySender: List<TransferResponse>?,
    val transfersByReceiver: List<TransferResponse>?,
    val createdAt: String?
) : Response
