package com.keshobank.mobile.data.prefs

import android.content.Context
import android.content.SharedPreferences

// Phase 3 target for vuln #1: this deliberately stays plain SharedPreferences
// (no EncryptedSharedPreferences / Keystore) once the vulnerability pass lands.
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_AUTH_TOKEN, value).apply()

    var accountNumber: String?
        get() = prefs.getString(KEY_ACCOUNT_NUMBER, null)
        set(value) = prefs.edit().putString(KEY_ACCOUNT_NUMBER, value).apply()

    var pinHash: String?
        get() = prefs.getString(KEY_PIN, null)
        set(value) = prefs.edit().putString(KEY_PIN, value).apply()

    val isLoggedIn: Boolean
        get() = !authToken.isNullOrEmpty()

    // Vuln #6's payoff: TxnAlertReceiver sets this on receiving a spoofed
    // OTP_CONFIRMED broadcast, and TransferConfirmActivity consumes it to
    // skip PIN entry entirely on the next transfer.
    var otpBypassGranted: Boolean
        get() = prefs.getBoolean(KEY_OTP_BYPASS, false)
        set(value) = prefs.edit().putBoolean(KEY_OTP_BYPASS, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "kesho_session"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_ACCOUNT_NUMBER = "account_number"
        private const val KEY_PIN = "pin_cipher"
        private const val KEY_OTP_BYPASS = "otp_bypass_granted"
    }
}
