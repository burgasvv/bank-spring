package org.burgas.bankspring.dao.card

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.NamedSubgraph
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.burgas.bankspring.dao.account.Account
import org.burgas.bankspring.dao.operation.Operation
import org.burgas.bankspring.dao.transfer.Transfer
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "card", schema = "public")
@NamedEntityGraph(
    name = "card-entity-graph",
    attributeNodes = [
        NamedAttributeNode(value = "operations"),
        NamedAttributeNode(value = "transfersBySender", subgraph = "transfersBySender-subgraph"),
        NamedAttributeNode(value = "transfersByReceiver", subgraph = "transfersByReceiver-subgraph"),
    ],
    subgraphs = [
        NamedSubgraph(
            name = "transfersBySender-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "sender"),
                NamedAttributeNode(value = "receiver"),
            ]
        ),
        NamedSubgraph(
            name = "transfersByReceiver-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "sender"),
                NamedAttributeNode(value = "receiver")
            ]
        )
    ]
)
class Card : org.burgas.bankspring.dao.Entity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    override lateinit var id: UUID

    @Column(name = "number")
    lateinit var number: String

    @Column(name = "code")
    var code: Long = 0

    @Column(name = "valid_until")
    lateinit var validUntil: LocalDate

    @Column(name = "balance")
    var balance: Double = 0.0

    @Column(name = "pin")
    lateinit var pin: String

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    lateinit var account: Account

    @OneToMany(mappedBy = "card", fetch = FetchType.LAZY)
    var operations: MutableList<Operation> = mutableListOf()

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    var transfersBySender: MutableList<Transfer> = mutableListOf()

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    var transfersByReceiver: MutableList<Transfer> = mutableListOf()

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime
}