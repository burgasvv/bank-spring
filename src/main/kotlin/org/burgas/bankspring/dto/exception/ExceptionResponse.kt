package org.burgas.bankspring.dto.exception

data class ExceptionResponse(
    val status: String,
    val code: Int,
    val message: String
)