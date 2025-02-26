package com.dmia.bioattendance.data.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


/*
 * Call init() function on first app’s run.
 *
 * Read & Write inside Activity or Fragment
 * PrefRepository.write(PrefRepository.TOKEN, "XXXX");//save string in shared preference.
 * PrefRepository.write(PrefRepository.ID_USER, "2523344");//save long in shared preference.
 */
object AppPreference {

    private lateinit var prefs: SharedPreferences

    private const val PREFS_NAME = "com.customer.bms"


    fun init(context: Context) {
        //prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        //Encrypted SharedPreference
        val masterKeyAlias = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    fun read(key: String, value: Boolean): Boolean {
        return prefs.getBoolean(key, value)
    }

    fun read(key: String, value: String): String? {
        return prefs.getString(key, value)
    }

    fun read(key: String, value: Long): Long {
        return prefs.getLong(key, value)
    }

    fun read(key: String, value: Int): Int {
        return prefs.getInt(key, value)
    }

    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putString(key, value)
            apply()
        }
    }

    fun write(key: String, value: Long) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putLong(key, value)
            apply()
        }
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putBoolean(key, value)
            apply()
        }
    }

    fun write(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putInt(key, value)
            apply()
        }
    }

    fun clearAll() {
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }


}