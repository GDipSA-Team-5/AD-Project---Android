package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.fragments.DisposeFormInput
import iss.nus.edu.sg.appfiles.mobile_ewaste.fragments.DisposeFormValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DisposeFormValidatorTest {

    @Test
    fun validate_returnsCategoryError_whenCategoryNotSelected() {
        val input = DisposeFormInput(0, 1, "SN-1", "0.5")

        val error = DisposeFormValidator.validate(input)

        assertEquals("Select category", error)
    }

    @Test
    fun validate_returnsItemTypeError_whenItemTypeNotSelected() {
        val input = DisposeFormInput(1, 0, "SN-1", "0.5")

        val error = DisposeFormValidator.validate(input)

        assertEquals("Select item type", error)
    }

    @Test
    fun validate_returnsSerialError_whenSerialMissing() {
        val input = DisposeFormInput(1, 1, " ", "0.5")

        val error = DisposeFormValidator.validate(input)

        assertEquals("Serial number required", error)
    }

    @Test
    fun validate_returnsWeightRequired_whenWeightMissing() {
        val input = DisposeFormInput(1, 1, "SN-1", "")

        val error = DisposeFormValidator.validate(input)

        assertEquals("Estimated weight required", error)
    }

    @Test
    fun validate_returnsInvalidWeight_whenWeightIsNotNumber() {
        val input = DisposeFormInput(1, 1, "SN-1", "abc")

        val error = DisposeFormValidator.validate(input)

        assertEquals("Invalid weight", error)
    }

    @Test
    fun validate_returnsNull_whenInputIsValid() {
        val input = DisposeFormInput(1, 1, "SN-1", "1.25")

        val error = DisposeFormValidator.validate(input)

        assertNull(error)
    }
}
