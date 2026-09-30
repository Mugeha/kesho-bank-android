package com.keshobank.mobile.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.keshobank.mobile.data.prefs.SessionManager

// Vuln #6: exported with no sender permission/signature check (see
// AndroidManifest.xml), so a local malicious app can spoof this broadcast
// and grant itself an OTP bypass:
//   adb shell am broadcast -a com.keshobank.mobile.action.OTP_CONFIRMED
// The next transfer confirmation then skips PIN entry entirely.
class TxnAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_OTP_CONFIRMED) {
            SessionManager(context).otpBypassGranted = true
        }
    }

    companion object {
        const val ACTION_OTP_CONFIRMED = "com.keshobank.mobile.action.OTP_CONFIRMED"
    }
}
