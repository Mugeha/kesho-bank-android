package com.keshobank.mobile.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.keshobank.mobile.data.local.KeshoDatabase
import com.keshobank.mobile.data.local.entities.TransactionEntity
import com.keshobank.mobile.databinding.ActivityAccountDetailsBinding
import com.keshobank.mobile.util.formatKes
import kotlinx.coroutines.launch

// Vuln #3: the search box's SQL is built by string concatenation, so
// crafted input breaks out of the query: sqlite_master enumeration and
// UNION SELECT extraction of the accounts table both work here.
class AccountDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountDetailsBinding
    private var accountId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountId = intent.getLongExtra(EXTRA_ACCOUNT_ID, -1)
        if (accountId == -1L) {
            finish()
            return
        }

        loadAccount()
        runSearch("")

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                runSearch(s?.toString().orEmpty())
            }
        })
    }

    private fun loadAccount() {
        val db = KeshoDatabase.getInstance(this)
        lifecycleScope.launch {
            db.accountDao().getPrimaryAccount()?.let { account ->
                binding.accountHeaderText.text =
                    "${account.accountHolderName}  ${account.accountNumber}"
                binding.nationalIdText.text = "ID: ${account.nationalId}"
            }
        }
    }

    private fun runSearch(query: String) {
        val db = KeshoDatabase.getInstance(this)
        val sql = "SELECT * FROM transactions WHERE accountId = $accountId " +
            "AND (counterpartyName LIKE '%$query%' OR memo LIKE '%$query%') " +
            "ORDER BY timestampMillis DESC"
        lifecycleScope.launch {
            val results = try {
                db.transactionDao().searchRaw(SimpleSQLiteQuery(sql))
            } catch (e: Exception) {
                emptyList<TransactionEntity>()
            }
            renderResults(results)
        }
    }

    private fun renderResults(results: List<TransactionEntity>) {
        binding.resultsContainer.removeAllViews()
        results.forEach { txn ->
            val row = TextView(this)
            val sign = if (txn.type == "CREDIT") "+" else "-"
            row.text = "${txn.counterpartyName}  $sign${formatKes(txn.amountCents)}  ${txn.memo}"
            row.setPadding(0, 8, 0, 8)
            binding.resultsContainer.addView(row)
        }
    }

    companion object {
        const val EXTRA_ACCOUNT_ID = "extra_account_id"
    }
}
