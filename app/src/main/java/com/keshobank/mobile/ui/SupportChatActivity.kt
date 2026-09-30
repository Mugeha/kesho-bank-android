package com.keshobank.mobile.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivitySupportChatBinding
import com.keshobank.mobile.webbridge.KeshoBridge

// Vuln #8: same JS-bridge overexposure as StatementWebViewActivity.
class SupportChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportChatBinding

    @Suppress("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(KeshoBridge(SessionManager(this)), "Android")
        binding.webView.loadUrl("file:///android_asset/support_chat.html")
    }
}
