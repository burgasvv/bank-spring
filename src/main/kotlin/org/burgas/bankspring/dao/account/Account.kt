package org.burgas.bankspring.dao.account

import jakarta.persistence.*
import org.burgas.bankspring.dao.card.Card
import org.burgas.bankspring.dao.wallet.Wallet
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "account", schema = "public")
@NamedEntityGraph(
    name = "account-entity-graph",
    attributeNodes = [
        NamedAttributeNode(value = "wallet"),
        NamedAttributeNode(value = "card")
    ]
)
class Account : org.burgas.bankspring.dao.Entity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    override lateinit var id: UUID

    @Column(name = "number")
    var number: Long = 0

    @Column(name = "inn")
    var inn: Long = 0

    @Column(name = "cpp")
    var cpp: Long = 0

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    lateinit var wallet: Wallet

    @OneToOne(mappedBy = "account", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    lateinit var card: Card

    lateinit var createdAt: LocalDateTime
}