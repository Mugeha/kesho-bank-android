package com.attacker.tapjack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.attacker.tapjack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var overlayShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.grantPermissionButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }

        binding.toggleOverlayButton.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                return@setOnClickListener
            }
            if (overlayShowing) {
                stopService(Intent(this, OverlayService::class.java))
                binding.toggleOverlayButton.setText(R.string.show_overlay)
            } else {
                startService(Intent(this, OverlayService::class.java))
                binding.toggleOverlayButton.setText(R.string.hide_overlay)
            }
            overlayShowing = !overlayShowing
        }
    }
}
