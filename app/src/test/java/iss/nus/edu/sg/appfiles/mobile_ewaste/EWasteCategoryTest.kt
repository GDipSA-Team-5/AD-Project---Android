package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter.EWasteCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class EWasteCategoryTest {

    @Test
    fun from_returnsMatchedCategory_ignoreCase() {
        val result = EWasteCategory.from("small household appliances")

        assertEquals(EWasteCategory.SMALL_HOUSEHOLD_APPLIANCES, result)
    }

    @Test
    fun from_returnsOther_whenNull() {
        val result = EWasteCategory.from(null)

        assertEquals(EWasteCategory.OTHER, result)
    }

    @Test
    fun from_returnsOther_whenUnknownName() {
        val result = EWasteCategory.from("Non Existing Category")

        assertEquals(EWasteCategory.OTHER, result)
    }

    @Test
    fun from_returnsConsumerElectronics_whenNameMatches() {
        val result = EWasteCategory.from("Consumer Electronics")

        assertEquals(EWasteCategory.CONSUMER_ELECTRONICS, result)
    }
}
