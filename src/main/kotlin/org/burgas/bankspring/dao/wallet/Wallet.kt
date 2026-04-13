package org.burgas.bankspring.dao.wallet

import jakarta.persistence.CascadeType
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
import org.burgas.bankspring.dao.identity.Identity
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "wallet", schema = "public")
@NamedEntityGraph(
    name = "wallet-entity-graph",
    attributeNodes = [
        NamedAttributeNode(value = "identity"),
        NamedAttributeNode(value = "accounts", subgraph = "accounts-subgraph")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "accounts-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "card")
            ]
        )
    ]
)
class Wallet : org.burgas.bankspring.dao.Entity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    override lateinit var id: UUID

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "identity_id", referencedColumnName = "id")
    lateinit var identity: Identity

    @OneToMany(mappedBy = "wallet", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var accounts: MutableList<Account> = mutableListOf()

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime
}