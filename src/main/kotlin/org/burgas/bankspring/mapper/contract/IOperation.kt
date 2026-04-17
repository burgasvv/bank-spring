package org.burgas.bankspring.mapper.contract

import org.burgas.bankspring.dao.operation.Operation
import org.burgas.bankspring.dao.operation.OperationType

interface IOperation {

    fun operate(operation: Operation) {
        if (operation.type == OperationType.DEPOSIT) {
            operation.card!!.balance += operation.amount

        } else {
            if (operation.card!!.balance >= operation.amount) {
                operation.card!!.balance -= operation.amount
            } else {
                throw IllegalArgumentException("Card balance not enough for WITHDRAW operation")
            }
        }
    }
}