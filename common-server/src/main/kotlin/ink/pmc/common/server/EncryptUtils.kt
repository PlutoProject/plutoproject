package ink.pmc.common.server

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Suppress("UNUSED")
fun encrypt(text: String): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    val secretKey = SecretKeySpec(token.toByteArray(), "AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedBytes = cipher.doFinal(text.toByteArray())
    return Base64.getEncoder().encodeToString(encryptedBytes)
}

@Suppress("UNUSED")
fun decrypt(encryptedText: String): String? {
    try {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        val secretKey = SecretKeySpec(token.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
        return String(decryptedBytes)
    } catch (e: Exception) {
        return null
    }
}