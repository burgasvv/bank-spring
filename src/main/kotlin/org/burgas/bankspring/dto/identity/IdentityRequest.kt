package org.burgas.bankspring.dto.identity

import org.burgas.bankspring.dao.identity.Authority
import org.burgas.bankspring.dto.Request
import java.util.UUID

data class IdentityRequest(
    override val id: UUID?,
    val authority: Authority?,
    val email: String?,
    val password: String?,
    val phone: String?,
    val status: Boolean?,
    val firstname: String?,
    val lastname: String?,
    val patronymic: String?
) : Request