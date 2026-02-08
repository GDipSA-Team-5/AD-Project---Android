package iss.nus.edu.sg.appfiles.mobile_ewaste.ui.rewards

import java.text.SimpleDateFormat
import java.util.Locale

object RewardsHistoryDateFormatter {
    fun format(raw: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            output.format(input.parse(raw)!!)
        } catch (_: Exception) {
            raw
        }
    }
}
