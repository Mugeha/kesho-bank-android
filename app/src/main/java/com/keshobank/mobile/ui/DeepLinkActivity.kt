package com.keshobank.mobile.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.R
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityDeepLinkBinding

// Vuln #7: handles keshobank://reset-pin?userId=&token= and
// keshobank://pay?to=&amount= with no ownership or session check, so a
// userId/token pair lifted off one device (e.g. via vuln #15's Logcat leak,
// or the vuln #5 provider) works from any other device with the app
// installed:
//   adb shell am start -a android.intent.action.VIEW \
//     -d "keshobank://reset-pin?userId=4010312345&token=anything"
class DeepLinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeepLinkBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        val uri = intent?.data
        when (uri?.host) {
            "reset-pin" -> handleResetPin(uri)
            "pay" -> handlePay(uri)
            else -> binding.deepLinkText.text = getString(R.string.deep_link_unrecognized)
        }
    }

    private fun handleResetPin(uri: Uri) {
        // No check that userId matches the logged-in account, and no
        // validation of token beyond presence.
        val token = uri.getQueryParameter("token")
        if (token.isNullOrBlank()) {
            binding.deepLinkText.text = getString(R.string.deep_link_unrecognized)
            return
        }
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()
    }

    private fun handlePay(uri: Uri) {
        val to = uri.getQueryParameter("to").orEmpty()
        val amount = uri.getQueryParameter("amount").orEmpty()
        startActivity(
            Intent(this, TransferActivity::class.java)
                .putExtra(TransferActivity.EXTRA_PREFILL_RECIPIENT, to)
                .putExtra(TransferActivity.EXTRA_PREFILL_AMOUNT, amount)
        )
        finish()
    }
}
