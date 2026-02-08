package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.fragments.HomeFragment
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeFragmentUnitTest {

    @Test
    fun co2SavedKg_returnsSeventyPercentOfEwasteWeight() {
        val fragment = HomeFragment()

        val result = fragment.co2SavedKg(10.0)

        assertEquals(7.0, result, 0.0001)
    }

    @Test
    fun co2SavedKg_handlesZeroInput() {
        val fragment = HomeFragment()

        val result = fragment.co2SavedKg(0.0)

        assertEquals(0.0, result, 0.0001)
    }
}
