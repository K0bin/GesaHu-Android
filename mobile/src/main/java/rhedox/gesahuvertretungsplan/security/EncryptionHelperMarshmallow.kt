package rhedox.gesahuvertretungsplan.security

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec

/**
 * Created by robin on 11.03.2018.
 */
@TargetApi(Build.VERSION_CODES.M)
class EncryptionHelperMarshmallow: EncryptionHelper {
    companion object {
        private const val keyAlias = "key"
        private const val androidKeyStore = "AndroidKeyStore"
        private const val transformationAlgorithm = "AES/CBC/PKCS7Padding"
    }

    private val key = retrieveKey()

    private fun retrieveKey(): Key {
        val keyStore = KeyStore.getInstance(androidKeyStore)
        keyStore.load(null)

        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeyStore)
            val keySpec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            keyGenerator.init(keySpec)
            return keyGenerator.generateKey()
        }
        return keyStore.getKey(keyAlias, null);
    }

    override fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(transformationAlgorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
        val ivString = Base64.encodeToString(cipher.iv, Base64.DEFAULT)
        return ivString + "|" + Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    override fun decrypt(text: String): String {
        val textParts = text.split("|")
        if (textParts.size != 2) return "";

        val iv = Base64.decode(textParts[0], Base64.DEFAULT)
        val passwordBytes = Base64.decode(textParts[1], Base64.DEFAULT)

        val cipher =  Cipher.getInstance(transformationAlgorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val decodedBytes = cipher.doFinal(passwordBytes)
        return String(decodedBytes, charset = StandardCharsets.UTF_8)
    }
}