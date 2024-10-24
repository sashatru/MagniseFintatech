package com.magnise.fintatech.data.remote.providers

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object EncryptedSharedPreferencesProvider {

    fun create(context: Context): EncryptedSharedPreferences {
        // Create the MasterKey using MasterKey.Builder
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM) // AES256-GCM is the recommended scheme
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs", // Name of the shared preferences file
            masterKey, // The generated master key
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }
}