package com.keshobank.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.keshobank.mobile.R
import com.keshobank.mobile.data.demo.DemoDataSeeder
import com.keshobank.mobile.data.local.KeshoDatabase
import com.keshobank.mobile.data.local.entities.AccountEntity
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityDashboardBinding
import com.keshobank.mobile.util.formatKes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private var currentAccount: AccountEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.transferButton.setOnClickListener {
            startActivity(Intent(this, TransferActivity::class.java))
        }
        binding.statementButton.setOnClickListener {
            currentAccount?.let { account ->
                startActivity(
                    Intent(this, StatementWebViewActivity::class.java)
                        .putExtra(StatementWebViewActivity.EXTRA_ACCOUNT_ID, account.id)
                )
            }
        }
        binding.supportButton.setOnClickListener {
            startActivity(Intent(this, SupportChatActivity::class.java))
        }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.viewAccountLink.setOnClickListener {
            currentAccount?.let { account ->
                startActivity(
                    Intent(this, AccountDetailsActivity::class.java)
                        .putExtra(AccountDetailsActivity.EXTRA_ACCOUNT_ID, account.id)
                )
            }
        }

        loadAccount()
    }

    private fun loadAccount() {
        val db = KeshoDatabase.getInstance(this)
        lifecycleScope.launch {
            var account = db.accountDao().getPrimaryAccount()
            if (account == null) {
                val seeded = DemoDataSeeder.buildPrimaryAccount()
                db.accountDao().insertAll(listOf(seeded))
                account = db.accountDao().getPrimaryAccount()
                account?.let { acc ->
                    db.transactionDao().insertAll(DemoDataSeeder.buildTransactionHistory(acc.id))
                }
            }
            currentAccount = account
            account?.let { renderAccount(it) }
        }
    }

    private fun renderAccount(account: AccountEntity) {
        binding.greetingText.text = getString(R.string.dashboard_greeting, account.accountHolderName)
        binding.balanceText.text = formatKes(account.balanceCents)
        binding.accountMetaText.text = getString(
            R.string.dashboard_account_meta,
            account.accountNumber,
            account.branchName
        )
        renderRecentTransactions(account.id)
    }

    private fun renderRecentTransactions(accountId: Long) {
        lifecycleScope.launch {
            val db = KeshoDatabase.getInstance(this@DashboardActivity)
            db.transactionDao().observeForAccount(accountId).first().take(5).forEach { txn ->
                val row = TextView(this@DashboardActivity)
                val sign = if (txn.type == "CREDIT") "+" else "-"
                row.text = "${txn.counterpartyName}  $sign${formatKes(txn.amountCents)}"
                row.setPadding(0, 8, 0, 8)
                binding.transactionListContainer.addView(row)
            }
        }
    }
}
