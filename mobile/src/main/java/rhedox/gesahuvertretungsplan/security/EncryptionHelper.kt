package rhedox.gesahuvertretungsplan.security

/**
 * Created by robin on 11.03.2018.
 */

interface EncryptionHelper {
    fun encrypt(text: String): String
    fun decrypt(text: String): String
}