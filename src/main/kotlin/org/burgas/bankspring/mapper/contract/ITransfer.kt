package org.burgas.bankspring.mapper.contract

import org.burgas.bankspring.dao.transfer.Transfer

interface ITransfer {

    fun transfer(transfer: Transfer) {
        if (transfer.amount > 0) {

            if (transfer.sender!!.balance >= transfer.amount) {
                transfer.sender!!.balance -= transfer.amount
                transfer.receiver!!.balance += transfer.amount

            } else {
                throw IllegalArgumentException("Not enough money for transfer")
            }

        } else {
            throw IllegalArgumentException("Wrong amount number, must be > 0")
        }
    }
}