package com.keshobank.mobile.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.R
import com.keshobank.mobile.data.crypto.HardcodedKeyPinCipher
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.data.remote.ApiClient
import com.keshobank.mobile.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var appPrefs: SharedPreferences
    private val pinCipher = HardcodedKeyPinCipher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)
        appPrefs = getSharedPreferences("kesho_app_settings", MODE_PRIVATE)

        binding.serverUrlInput.setText(
            appPrefs.getString(KEY_SERVER_URL, ApiClient.DEFAULT_BASE_URL)
        )
        binding.pinStatusText.text = if (sessionManager.pinHash != null) {
            getString(R.string.settings_pin_saved)
        } else {
            ""
        }

        binding.saveServerUrlButton.setOnClickListener {
            appPrefs.edit()
                .putString(KEY_SERVER_URL, binding.serverUrlInput.text.toString())
                .apply()
        }

        binding.savePinButton.setOnClickListener {
            val newPin = binding.newPinInput.text.toString()
            val confirmPin = binding.confirmPinInput.text.toString()
            if (newPin.length == 4 && newPin == confirmPin) {
                Log.d("KeshoPin", "PIN set to $newPin")
                sessionManager.pinHash = pinCipher.encrypt(newPin)
                binding.pinStatusText.text = getString(R.string.settings_pin_saved)
                binding.newPinInput.text?.clear()
                binding.confirmPinInput.text?.clear()
            } else {
                binding.pinStatusText.text = getString(R.string.settings_pin_mismatch)
            }
        }

        binding.viewRoeButton.setOnClickListener {
            startActivity(
                Intent(this, RoeActivity::class.java)
                    .putExtra(RoeActivity.EXTRA_FROM_SETTINGS, true)
            )
        }

        binding.logoutButton.setOnClickListener {
            sessionManager.clear()
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    companion object {
        private const val KEY_SERVER_URL = "server_url"
    }
}
