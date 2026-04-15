package org.burgas.bankspring.service.contract

import org.burgas.bankspring.dao.Entity
import org.burgas.bankspring.dto.Request
import org.burgas.bankspring.dto.Response
import java.util.UUID

interface CrudService<in R : Request, E : Entity, out F : Response> : EntityService<E> {

    fun findById(id: UUID): F

    fun create(request: R)

    fun update(request: R)

    fun delete(id: UUID)
}