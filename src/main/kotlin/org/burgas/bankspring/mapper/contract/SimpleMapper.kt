package org.burgas.bankspring.mapper.contract

import org.burgas.bankspring.dao.Entity
import org.burgas.bankspring.dto.Request
import org.burgas.bankspring.dto.Response

interface SimpleMapper<in R : Request, E : Entity, out F : Response> {

    fun toEntity(request: R): E

    fun toResponse(entity: E): F
}