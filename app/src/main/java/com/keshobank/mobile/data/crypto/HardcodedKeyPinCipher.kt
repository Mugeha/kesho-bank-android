package com.keshobank.mobile.data.crypto

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

// Vuln #12: AES/ECB with a key baked into the APK (recoverable via jadx),
// swapped in for KeystorePinCipher. ECB also leaks equal-PIN patterns
// (identical plaintext blocks -> identical ciphertext blocks), and since the
// key is static, any stored PIN cipher pulled off the device decrypts
// offline with no device interaction at all.
class HardcodedKeyPinCipher : PinCipher {

    private val key = SecretKeySpec(HARDCODED_KEY.toByteArray(Charsets.UTF_8), "AES")

    override fun encrypt(pin: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.encodeToString(cipher.doFinal(pin.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }

    override fun decrypt(cipherText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP)), Charsets.UTF_8)
    }

    companion object {
        private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
        private const val HARDCODED_KEY = "KeshoBank1234567"
    }
}
