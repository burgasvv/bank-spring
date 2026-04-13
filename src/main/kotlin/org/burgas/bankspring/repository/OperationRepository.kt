package org.burgas.bankspring.repository

import org.burgas.bankspring.dao.operation.Operation
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface OperationRepository : JpaRepository<Operation, UUID> {

    @EntityGraph(value = "operation-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    override fun findById(id: UUID): Optional<Operation>
}