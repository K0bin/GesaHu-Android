@file:Suppress("DEPRECATION")

package rhedox.gesahuvertretungsplan.security

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import com.crashlytics.android.Crashlytics
import org.joda.time.DateTime
import org.joda.time.Period
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal


/**
 * Created by robin on 11.03.2018.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class EncryptionHelperJellyBean(context: Context): EncryptionHelper {
    companion object {
        private const val keyAlias = "key"
        private const val androidKeyStore = "AndroidKeyStore"
        private const val keyX500Principal = "CN=GesaHu, O=K0bin, C=Germany";
        private const val transformationAlgorithm = "RSA/ECB/PKCS1Padding"
        private val provider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) "AndroidKeyStoreBCWorkaround" else "AndroidOpenSSL"
        private const val prefix = "/|\\ENCRYPTED/|\\"
    }

    private var isNewKey = false
    private val key = retrieveKey(context)

    private fun retrieveKey(context: Context): KeyPair {
        val keyStore = KeyStore.getInstance(androidKeyStore)
        keyStore.load(null)

        if (!keyStore.containsAlias(keyAlias)) {
            isNewKey = true
            val start = DateTime.now().toDate()
            val end = DateTime.now().withPeriodAdded(Period.years(99), 1).toDate()

            val keyGenerator = KeyPairGenerator.getInstance("RSA", androidKeyStore)
            val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(keyAlias)
                    .setSubject(X500Principal(keyX500Principal))
                    .setSerialNumber(10.toBigInteger())
                    .setStartDate(start)
                    .setEndDate(end)
                    .build()
            keyGenerator.initialize(spec)
            return keyGenerator.generateKeyPair()
        }
        val entry = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
        return KeyPair(entry.certificate.publicKey, entry.privateKey);
    }

    override fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(transformationAlgorithm, provider)
        cipher.init(Cipher.ENCRYPT_MODE, key.public)
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charset.forName("UTF-8")))
        return prefix + Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    override fun decrypt(text: String): String? {
        if (isNewKey) return null //the original key somehow got lost so its impossible to decrypt
        if (!text.startsWith(prefix)) return text //password is unencrypted
        if (text.length - prefix.length < 1) return null //only prefix

        val passwordBytes = Base64.decode(text.substring(prefix.length), Base64.DEFAULT)

        return try {
            val cipher = Cipher.getInstance(transformationAlgorithm, provider)
            cipher.init(Cipher.DECRYPT_MODE, key.private)
            val decodedBytes = cipher.doFinal(passwordBytes)
            String(decodedBytes, charset = Charset.forName("UTF-8"))
        } catch (e: GeneralSecurityException) {
            Crashlytics.logException(e)
            null
        } catch (e: SecurityException) {
            Crashlytics.logException(e)
            null
        }
    }
}