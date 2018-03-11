package rhedox.gesahuvertretungsplan.security

/**
 * People who still run Android 4.1 or 4.2 have bigger security problems than their
 * GesaHu password not being encrypted
 * Created by robin on 11.03.2018.
 */

class NoEncryptionHelper: EncryptionHelper {
    override fun encrypt(text: String): String = text

    override fun decrypt(text: String): String = text
}