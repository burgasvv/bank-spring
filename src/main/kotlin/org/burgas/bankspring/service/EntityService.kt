package org.burgas.bankspring.service

import org.burgas.bankspring.dao.Entity
import java.util.UUID

interface EntityService<out E : Entity> {

    fun findEntity(id: UUID): E
}