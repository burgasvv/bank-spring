package org.burgas.bankspring.repository

import org.burgas.bankspring.dao.card.Card
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface CardRepository : JpaRepository<Card, UUID> {

    @EntityGraph(value = "card-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    override fun findById(id: UUID): Optional<Card>
}