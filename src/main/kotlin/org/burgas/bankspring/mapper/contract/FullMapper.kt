package org.burgas.bankspring.mapper.contract

import org.burgas.bankspring.dao.Entity
import org.burgas.bankspring.dto.Request
import org.burgas.bankspring.dto.Response

interface FullMapper<in R : Request, E : Entity, out S : Response, out F : Response> {

    fun toEntity(request: R): E

    fun toShortResponse(entity: E): S

    fun toFullResponse(entity: E): F
}