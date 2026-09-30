package com.keshobank.mobile.util

import java.io.File

// Phase 3 target for vuln #13: this check (and the transfer-limit logic that
// consults it) lives entirely on-device with no server-side enforcement, so
// it's bypassable via Frida hooking or direct smali patching.
object RootCheck {

    private val suspiciousPaths = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su"
    )

    fun isDeviceRooted(): Boolean = suspiciousPaths.any { File(it).exists() }
}
