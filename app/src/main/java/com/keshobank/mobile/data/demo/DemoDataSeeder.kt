package com.keshobank.mobile.data.demo

import com.keshobank.mobile.data.local.entities.AccountEntity
import com.keshobank.mobile.data.local.entities.TransactionEntity
import kotlin.random.Random

// All names, IDs, account and branch numbers below are synthetic, generated
// for training purposes only. None correspond to real people, accounts, or
// Kesho Bank branches. Kesho Bank itself is a fictional entity.
object DemoDataSeeder {

    private val branches = listOf(
        "KSB-001" to "Nairobi CBD",
        "KSB-014" to "Kisumu Oginga",
        "KSB-022" to "Mombasa Nyali",
        "KSB-031" to "Eldoret Town",
        "KSB-045" to "Nakuru Westside"
    )

    private val demoFirstNames = listOf(
        "Amani", "Baraka", "Chiku", "Denis", "Faraja", "Imani", "Juma",
        "Kanini", "Lulu", "Mwangi", "Njeri", "Otieno", "Pendo", "Wanjiru"
    )

    private val demoLastNames = listOf(
        "Kariuki", "Otieno", "Mwangi", "Njoroge", "Achieng", "Kiprop",
        "Wafula", "Mutua", "Chebet", "Barasa"
    )

    private val counterparties = listOf(
        "Jua Kali Traders" to "4010556621",
        "Safari Mart Supermarket" to "4010778213",
        "Bidii Sacco" to "4010991045",
        "Nuru Utilities Co." to "4010102938",
        "Tumaini Logistics" to "4010446701"
    )

    // Fixed so the training material can document a working demo login.
    const val DEMO_ACCOUNT_NUMBER = "4010312345"
    const val DEMO_PASSWORD = "Kesho@Demo2026"

    fun buildPrimaryAccount(): AccountEntity {
        val (branchCode, branchName) = branches.random()
        val holder = "${demoFirstNames.random()} ${demoLastNames.random()}"
        return AccountEntity(
            accountNumber = DEMO_ACCOUNT_NUMBER,
            accountHolderName = holder,
            nationalId = "${Random.nextInt(10000000, 39999999)}",
            branchCode = branchCode,
            branchName = branchName,
            accountTier = listOf("STANDARD", "GOLD", "PLATINUM").random(),
            dailyLimitCents = 15_000_00,
            balanceCents = Random.nextLong(5_000_00, 480_000_00)
        )
    }

    fun buildTransactionHistory(accountId: Long, count: Int = 25): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        return (0 until count).map { i ->
            val (name, acctNo) = counterparties.random()
            val isCredit = Random.nextBoolean()
            TransactionEntity(
                accountId = accountId,
                type = if (isCredit) "CREDIT" else "DEBIT",
                counterpartyName = name,
                counterpartyAccountNumber = acctNo,
                amountCents = Random.nextLong(500_00, 45_000_00),
                memo = if (isCredit) "Payment received" else "Payment sent",
                timestampMillis = now - i * 36_000_00L,
                status = "COMPLETED"
            )
        }
    }
}
