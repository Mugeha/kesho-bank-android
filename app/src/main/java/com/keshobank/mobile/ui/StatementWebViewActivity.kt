package com.keshobank.mobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityStatementWebViewBinding
import com.keshobank.mobile.webbridge.KeshoBridge

// Vuln #8: addJavascriptInterface exposes KeshoBridge to whatever JS this
// WebView loads. Today that's a bundled asset, but any JS reachable here,
// including via a redirect or injected content, can call
// Android.getAuthToken()/getAccountNumber().
class StatementWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatementWebViewBinding

    @Suppress("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatementWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(KeshoBridge(SessionManager(this)), "Android")
        binding.webView.loadUrl("file:///android_asset/statement.html")
    }

    companion object {
        const val EXTRA_ACCOUNT_ID = "extra_account_id"
    }
}
