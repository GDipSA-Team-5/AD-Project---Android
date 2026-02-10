package iss.nus.edu.sg.appfiles.mobile_ewaste.data

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLogin(userId: Int?) {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId ?: -1)
        }
    }

    fun clear() {
        prefs.edit {
            clear()
        }
    }


    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun userId(): Int? {
        val value = prefs.getInt(KEY_USER_ID, -1)
        return if (value == -1) null else value
    }

    private companion object {
        private const val PREFS_NAME = "mobile_ewaste_session"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
    }
}
