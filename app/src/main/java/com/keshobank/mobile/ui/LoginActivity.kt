package com.keshobank.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.data.demo.DemoDataSeeder
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityLoginBinding

// Validates against the fixed, documented demo credential (there's no
// registration flow in this training app, so one seeded account is enough to
// exercise every screen). Phase 4's optional backend adds a real network
// login call on top of this for vuln #18 (credentials sent via URL).
//
// Vuln #15: the plaintext password lands in Logcat on every attempt.
// `adb logcat -s KeshoLogin` reads it straight off the device, no root needed.
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.loginButton.setOnClickListener {
            val accountNumber = binding.accountNumberInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            Log.d("KeshoLogin", "Login attempt: account=$accountNumber password=$password")

            if (accountNumber == DemoDataSeeder.DEMO_ACCOUNT_NUMBER &&
                password == DemoDataSeeder.DEMO_PASSWORD
            ) {
                binding.errorText.visibility = android.view.View.GONE
                sessionManager.authToken = "demo-session-${System.currentTimeMillis()}"
                sessionManager.accountNumber = accountNumber
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                binding.errorText.visibility = android.view.View.VISIBLE
            }
        }
    }
}
