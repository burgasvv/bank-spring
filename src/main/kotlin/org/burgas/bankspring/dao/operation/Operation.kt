package org.burgas.bankspring.dao.operation

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.NamedSubgraph
import jakarta.persistence.Table
import org.burgas.bankspring.dao.card.Card
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "operation", schema = "public")
@NamedEntityGraph(
    name = "operation-entity-graph",
    attributeNodes = [
        NamedAttributeNode(value = "card", subgraph = "card-subgraph")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "card-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "account", subgraph = "account-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "account-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "wallet", subgraph = "wallet-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "wallet-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "identity")
            ]
        )
    ]
)
class Operation : org.burgas.bankspring.dao.Entity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    override lateinit var id: UUID

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    lateinit var type: OperationType

    @Column(name = "amount")
    var amount: Double = 0.0

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    var card: Card? = null

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime
}