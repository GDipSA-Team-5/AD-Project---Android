package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.rewards.RewardsHistoryDateFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardsHistoryDateFormatterTest {

    @Test
    fun format_returnsReadableDate_whenInputIsIsoDateTime() {
        val result = RewardsHistoryDateFormatter.format("2026-02-08T14:35:00")

        assertTrue(result.contains("2026"))
        assertTrue(result.contains(":"))
    }

    @Test
    fun format_returnsRawValue_whenInputIsInvalid() {
        val raw = "not-a-date"

        val result = RewardsHistoryDateFormatter.format(raw)

        assertEquals(raw, result)
    }
}
