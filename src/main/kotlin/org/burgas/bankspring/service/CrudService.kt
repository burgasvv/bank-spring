package org.burgas.bankspring.service

import org.burgas.bankspring.dto.Request
import org.burgas.bankspring.dto.Response
import java.util.UUID

interface CrudService<in R : Request, out F : Response> {

    fun findById(id: UUID): F

    fun create(request: R)

    fun update(request: R)

    fun delete(id: UUID)
}