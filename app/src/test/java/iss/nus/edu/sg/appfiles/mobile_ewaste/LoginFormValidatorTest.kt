package iss.nus.edu.sg.appfiles.mobile_ewaste

import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.LoginFormValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginFormValidatorTest {

    @Test
    fun validate_returnsEmailRequired_whenEmailIsBlank() {
        val result = LoginFormValidator.validate(email = "   ", password = "secret")

        assertEquals("Email is required", result.emailError)
        assertEquals(null, result.passwordError)
        assertTrue(!result.isValid)
    }

    @Test
    fun validate_returnsInvalidEmail_whenEmailIsMalformed() {
        val result = LoginFormValidator.validate(email = "not-an-email", password = "secret")

        assertEquals("Invalid email", result.emailError)
        assertEquals(null, result.passwordError)
        assertTrue(!result.isValid)
    }

    @Test
    fun validate_returnsPasswordRequired_whenPasswordIsBlank() {
        val result = LoginFormValidator.validate(email = "user@test.com", password = "")

        assertEquals(null, result.emailError)
        assertEquals("Password is required", result.passwordError)
        assertTrue(!result.isValid)
    }

    @Test
    fun validate_returnsValid_whenEmailAndPasswordAreValid() {
        val result = LoginFormValidator.validate(email = "user@test.com", password = "secret")

        assertEquals(null, result.emailError)
        assertEquals(null, result.passwordError)
        assertTrue(result.isValid)
    }
}
