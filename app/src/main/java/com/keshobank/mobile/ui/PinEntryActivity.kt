package com.keshobank.mobile.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.R
import com.keshobank.mobile.data.crypto.HardcodedKeyPinCipher
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityPinEntryBinding

// Verifies against SessionManager.pinHash (Keystore-encrypted). Reachable
// both from the transfer-confirm flow and, once Phase 3 marks it exported,
// directly via adb/another app with no prior session check.
class PinEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinEntryBinding
    private lateinit var sessionManager: SessionManager
    private val pinCipher = HardcodedKeyPinCipher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        val storedCipher = sessionManager.pinHash
        if (storedCipher == null) {
            startActivity(Intent(this, SettingsActivity::class.java))
            finish()
            return
        }

        binding.submitPinButton.setOnClickListener {
            val entered = binding.pinInput.text.toString()
            val matches = runCatching { pinCipher.decrypt(storedCipher) == entered }.getOrDefault(false)
            if (matches) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                binding.pinInput.error = getString(R.string.pin_entry_incorrect)
            }
        }
    }
}
