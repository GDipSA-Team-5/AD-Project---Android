package iss.nus.edu.sg.appfiles.mobile_ewaste.testutil

import android.content.Context

object SessionTestUtil {
    fun login(context: Context, userId: Int = 1) {
        // Use commit() so the Activity can read immediately.
        context.getSharedPreferences("mobile_ewaste_session", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("logged_in", true)
            .putInt("user_id", userId)
            .commit()
    }

    fun logout(context: Context) {
        context.getSharedPreferences("mobile_ewaste_session", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }
}
