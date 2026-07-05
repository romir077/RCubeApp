package com.rcube.app.data.local

import android.content.Context
import com.rcube.app.data.model.Mode

/**
 * Persists the signed-in session across app restarts (SharedPreferences, app-private).
 * Stores the backend token + enough session state to skip the auth flow on relaunch.
 *
 * Note: for production, move the token to EncryptedSharedPreferences.
 */
class SessionStore(context: Context) {

    private val prefs = context.getSharedPreferences("rcube_session", Context.MODE_PRIVATE)

    data class Persisted(
        val phone: String,
        val firstName: String,
        val lastName: String,
        val aadhaarStatus: String,
        val mode: Mode,
        val needsModeSelection: Boolean,
        val token: String?,
    )

    fun save(
        phone: String,
        firstName: String,
        lastName: String,
        aadhaarStatus: String,
        mode: Mode,
        needsModeSelection: Boolean,
        token: String?,
    ) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_PHONE, phone)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .putString(KEY_AADHAAR, aadhaarStatus)
            .putString(KEY_MODE, mode.name)
            .putBoolean(KEY_NEEDS_MODE, needsModeSelection)
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun load(): Persisted? {
        if (!prefs.getBoolean(KEY_LOGGED_IN, false)) return null
        return Persisted(
            phone = prefs.getString(KEY_PHONE, "").orEmpty(),
            firstName = prefs.getString(KEY_FIRST_NAME, "").orEmpty(),
            lastName = prefs.getString(KEY_LAST_NAME, "").orEmpty(),
            aadhaarStatus = prefs.getString(KEY_AADHAAR, "NOT_SUBMITTED").orEmpty(),
            mode = runCatching { Mode.valueOf(prefs.getString(KEY_MODE, Mode.CREATOR.name)!!) }
                .getOrDefault(Mode.CREATOR),
            needsModeSelection = prefs.getBoolean(KEY_NEEDS_MODE, false),
            token = prefs.getString(KEY_TOKEN, null),
        )
    }

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val KEY_LOGGED_IN = "logged_in"
        const val KEY_PHONE = "phone"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_AADHAAR = "aadhaar_status"
        const val KEY_MODE = "mode"
        const val KEY_NEEDS_MODE = "needs_mode"
        const val KEY_TOKEN = "token"
    }
}
