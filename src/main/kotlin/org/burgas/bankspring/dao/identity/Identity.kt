package org.burgas.bankspring.dao.identity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.NamedSubgraph
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.burgas.bankspring.dao.wallet.Wallet
import java.util.UUID

@Entity
@Table(name = "identity", schema = "public")
@NamedEntityGraph(
    name = "identity-entity-graph",
    attributeNodes = [
        NamedAttributeNode(value = "wallet", subgraph = "wallet-subgraph")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "wallet-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "accounts", subgraph = "accounts-subgraph")
            ]
        ),
        NamedSubgraph(
            name = "accounts-subgraph",
            attributeNodes = [
                NamedAttributeNode(value = "card")
            ]
        )
    ]
)
class Identity : org.burgas.bankspring.dao.Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    override lateinit var id: UUID

    @Column(name = "authority")
    lateinit var authority: Authority

    @Column(name = "email")
    lateinit var email: String

    @Column(name = "password")
    lateinit var password: String

    @Column(name = "phone")
    lateinit var phone: String

    @Column(name = "status")
    var status: Boolean = true

    @Column(name = "firstname")
    lateinit var firstname: String

    @Column(name = "lastname")
    lateinit var lastname: String

    @Column(name = "patronymic")
    lateinit var patronymic: String

    @OneToOne(mappedBy = "identity", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    lateinit var wallet: Wallet
}