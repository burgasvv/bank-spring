package org.burgas.bankspring.service.contract

import org.burgas.bankspring.dto.Response

interface ListService<out S : Response> {

    fun findAll(): List<S>
}