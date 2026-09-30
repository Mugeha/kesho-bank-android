package com.keshobank.mobile.data.crypto

// Phase 2 wires this to real Android Keystore-backed AES/GCM.
// Phase 3's vulnerability pass deliberately regresses it to a
// hardcoded-key AES/ECB implementation for vuln #12.
interface PinCipher {
    fun encrypt(pin: String): String
    fun decrypt(cipherText: String): String
}
