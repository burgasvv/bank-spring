package org.burgas.bankspring.router.contract

import org.burgas.bankspring.service.contract.Service

interface Router<S : Service> {

    val service: S
}