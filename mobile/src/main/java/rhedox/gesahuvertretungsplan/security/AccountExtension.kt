package rhedox.gesahuvertretungsplan.security

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle

/**
 * Created by robin on 11.03.2018.
 */
fun AccountManager.addAccountSecurely(account: Account, password: String, helper: EncryptionHelper) = addAccountExplicitly(account, helper.encrypt(password), Bundle())

fun AccountManager.getPasswordSecurely(account: Account, helper: EncryptionHelper): String? {
    val password = getPassword(account) ?: return null
    return helper.decrypt(password)
}

fun AccountManager.setPasswordSecurely(account: Account, password: String, helper: EncryptionHelper) = setPassword(account, helper.encrypt(password))

fun AccountManager.encryptExistingPassword(account: Account, encryptionHelper: EncryptionHelper) {
    val password = getPassword(account) ?: return
    setPassword(account, encryptionHelper.encrypt(password))
}