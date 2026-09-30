package com.keshobank.mobile.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.keshobank.mobile.data.prefs.SessionManager
import com.keshobank.mobile.databinding.ActivityRoeBinding

class RoeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromSettings = intent.getBooleanExtra(EXTRA_FROM_SETTINGS, false)
        val sessionManager = SessionManager(this)

        binding.acknowledgeButton.setOnClickListener {
            when {
                fromSettings -> finish()
                sessionManager.isLoggedIn -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
                else -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    companion object {
        const val EXTRA_FROM_SETTINGS = "extra_from_settings"
    }
}
