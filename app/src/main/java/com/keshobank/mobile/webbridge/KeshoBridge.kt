package com.keshobank.mobile.webbridge

import android.webkit.JavascriptInterface
import com.keshobank.mobile.data.prefs.SessionManager

// Phase 3 target for vuln #8: attached via addJavascriptInterface() to the
// support-chat/e-statement WebViews, exposing session data to any JS the
// WebView loads (including third-party content in the WebView, if reachable).
class KeshoBridge(private val sessionManager: SessionManager) {

    @JavascriptInterface
    fun getAuthToken(): String = sessionManager.authToken.orEmpty()

    @JavascriptInterface
    fun getAccountNumber(): String = sessionManager.accountNumber.orEmpty()
}
