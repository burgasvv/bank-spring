package org.burgas.bankspring.repository

import org.burgas.bankspring.dao.transfer.Transfer
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface TransferRepository : JpaRepository<Transfer, UUID> {

    @EntityGraph(value = "transfer-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    override fun findById(id: UUID): Optional<Transfer>
}