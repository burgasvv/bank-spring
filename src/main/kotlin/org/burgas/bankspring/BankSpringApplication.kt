package org.burgas.bankspring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankSpringApplication

fun main(args: Array<String>) {
    runApplication<BankSpringApplication>(*args)
}
