package com.keshobank.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.keshobank.mobile.R
import com.keshobank.mobile.data.local.KeshoDatabase
import com.keshobank.mobile.data.local.entities.TransactionEntity
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityTransferConfirmBinding
import com.keshobank.mobile.util.RootCheck
import com.keshobank.mobile.util.formatKes
import kotlinx.coroutines.launch

// Vuln #4: exported (see AndroidManifest.xml), launchable directly with just
// recipient/amount/memo extras, skipping TransferActivity, PinEntryActivity,
// and login entirely:
//   adb shell am start -n com.keshobank.mobile/.ui.TransferConfirmActivity \
//     --es extra_recipient 4010999999 --es extra_amount 50000 --es extra_memo pwned
//
// The daily-limit and root checks below are vuln #13 by construction: both
// live purely in this on-device code path with no server-side backstop, so
// they're bypassable via Frida hooking or direct smali patching.
class TransferConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferConfirmBinding
    private lateinit var sessionManager: SessionManager
    private var recipient = ""
    private var amountCents = 0L
    private var memo = ""

    private val pinLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            recordTransfer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        recipient = intent.getStringExtra(EXTRA_RECIPIENT).orEmpty()
        val amountText = intent.getStringExtra(EXTRA_AMOUNT).orEmpty()
        memo = intent.getStringExtra(EXTRA_MEMO).orEmpty()
        amountCents = ((amountText.toDoubleOrNull() ?: 0.0) * 100).toLong()

        binding.summaryText.text = "To: $recipient\nAmount: KES $amountText\nMemo: $memo"

        binding.confirmButton.setOnClickListener {
            lifecycleScope.launch { checkLimitAndProceed() }
        }
    }

    private suspend fun checkLimitAndProceed() {
        val db = KeshoDatabase.getInstance(this)
        val account = db.accountDao().getPrimaryAccount()
        if (account == null) {
            binding.statusText.text = getString(R.string.transfer_no_account)
            return
        }
        if (RootCheck.isDeviceRooted()) {
            binding.statusText.text = getString(R.string.transfer_rooted_warning)
            return
        }
        if (amountCents > account.dailyLimitCents) {
            binding.statusText.text = getString(
                R.string.transfer_limit_exceeded,
                formatKes(account.dailyLimitCents)
            )
            return
        }
        if (amountCents > account.balanceCents) {
            binding.statusText.text = getString(R.string.transfer_insufficient_funds)
            return
        }

        // Vuln #6's payoff: a spoofed OTP_CONFIRMED broadcast sets this flag,
        // letting this transfer skip PIN entry entirely.
        if (sessionManager.otpBypassGranted) {
            sessionManager.otpBypassGranted = false
            recordTransfer()
            return
        }

        pinLauncher.launch(Intent(this, PinEntryActivity::class.java))
    }

    private fun recordTransfer() {
        val db = KeshoDatabase.getInstance(this)
        lifecycleScope.launch {
            val account = db.accountDao().getPrimaryAccount() ?: return@launch
            db.transactionDao().insertAll(
                listOf(
                    TransactionEntity(
                        accountId = account.id,
                        type = "DEBIT",
                        counterpartyName = recipient,
                        counterpartyAccountNumber = recipient,
                        amountCents = amountCents,
                        memo = memo,
                        timestampMillis = System.currentTimeMillis(),
                        status = "COMPLETED"
                    )
                )
            )
            db.accountDao().updateBalance(account.id, account.balanceCents - amountCents)
            startActivity(Intent(this@TransferConfirmActivity, DashboardActivity::class.java))
            finish()
        }
    }

    companion object {
        const val EXTRA_RECIPIENT = "extra_recipient"
        const val EXTRA_AMOUNT = "extra_amount"
        const val EXTRA_MEMO = "extra_memo"
    }
}
