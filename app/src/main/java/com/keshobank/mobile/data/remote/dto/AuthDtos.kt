package com.keshobank.mobile.data.remote.dto

data class LoginResponse(
    val token: String,
    val accountNumber: String,
    val accountHolderName: String
)

data class StatementResponse(
    val accountId: String,
    val transactions: List<StatementTransactionDto>
)

data class StatementTransactionDto(
    val id: String,
    val counterpartyName: String,
    val amountCents: Long,
    val memo: String,
    val timestampMillis: Long
)
