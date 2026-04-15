package org.burgas.bankspring.dao.transfer

import jakarta.persistence.*
import org.burgas.bankspring.dao.card.Card
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "transfer", schema = "public")
@NamedEntityGraph(
    name = "transfer-entity-graph",
    attributeNodes = [
        NamedAttributeNode(value = "sender", subgraph = "sender-subgraph"),
        NamedAttributeNode(value = "receiver", subgraph = "receiver-subgraph")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "sender-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "account", subgraph = "sender-account-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "sender-account-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "wallet", subgraph = "sender-wallet-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "sender-wallet-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "identity")
            ]
        ),
        NamedSubgraph(
            name = "receiver-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "account", subgraph = "receiver-account-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "receiver-account-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "wallet", subgraph = "receiver-wallet-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "receiver-wallet-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "identity")
            ]
        )
    ]
)
class Transfer : org.burgas.bankspring.dao.Entity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    override lateinit var id: UUID

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    var sender: Card? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    var receiver: Card? = null

    @Column(name = "amount")
    var amount: Double = 0.0

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime
}