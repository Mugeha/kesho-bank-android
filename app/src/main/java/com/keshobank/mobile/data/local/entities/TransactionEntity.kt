package com.keshobank.mobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long,
    val type: String,
    val counterpartyName: String,
    val counterpartyAccountNumber: String,
    val amountCents: Long,
    val currency: String = "KES",
    val memo: String,
    val timestampMillis: Long,
    val status: String
)
