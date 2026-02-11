package iss.nus.edu.sg.appfiles.mobile_ewaste.data

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLogin(userId: Int?, token: String?) {
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putInt(KEY_USER_ID, userId ?: -1)
            putString(KEY_TOKEN, token)
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

    fun token(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveProfile(phoneNumber: String?, regionId: Int?, regionName: String?) {
        prefs.edit {
            putString(KEY_PHONE, phoneNumber)
            putInt(KEY_REGION_ID, regionId ?: -1)
            putString(KEY_REGION_NAME, regionName)
        }
    }

    fun phoneNumber(): String? = prefs.getString(KEY_PHONE, null)
    fun regionId(): Int? {
        val value = prefs.getInt(KEY_REGION_ID, -1)
        return if (value == -1) null else value
    }
    fun regionName(): String? = prefs.getString(KEY_REGION_NAME, null)

    private companion object {
        private const val PREFS_NAME = "mobile_ewaste_session"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_TOKEN = "token"
        private const val KEY_PHONE = "phone"
        private const val KEY_REGION_ID = "region_id"
        private const val KEY_REGION_NAME = "region_name"
    }
}
