package com.keshobank.mobile.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountNumber: String,
    val accountHolderName: String,
    val nationalId: String,
    val branchCode: String,
    val branchName: String,
    val accountTier: String,
    val dailyLimitCents: Long,
    val balanceCents: Long,
    val currency: String = "KES"
)
