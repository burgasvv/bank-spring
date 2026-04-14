package org.burgas.bankspring.repository

import org.burgas.bankspring.dao.account.Account
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface AccountRepository : JpaRepository<Account, UUID> {

    @EntityGraph(value = "account-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    override fun findById(id: UUID): Optional<Account>

    fun existsAccountByNumber(number: Long): Boolean

    fun existsAccountByInn(inn: Long): Boolean

    fun existsAccountByCpp(cpp: Long): Boolean
}