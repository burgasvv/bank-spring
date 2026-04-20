package org.burgas.bankspring.dto.identity

import org.burgas.bankspring.dao.identity.Authority
import org.burgas.bankspring.dto.Request
import java.util.UUID

data class IdentityRequest(
    override val id: UUID? = null,
    val authority: Authority? = null,
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val status: Boolean? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val patronymic: String? = null
) : Request