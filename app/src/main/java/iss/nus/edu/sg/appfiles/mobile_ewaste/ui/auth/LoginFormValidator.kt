package iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth

import java.util.regex.Pattern

data class LoginValidationResult(
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isValid: Boolean get() = emailError == null && passwordError == null
}

object LoginFormValidator {
    private val EMAIL_PATTERN: Pattern =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun validate(email: String, password: String): LoginValidationResult {
        val trimmedEmail = email.trim()

        val emailError = when {
            trimmedEmail.isEmpty() -> "Email is required"
            !EMAIL_PATTERN.matcher(trimmedEmail).matches() -> "Invalid email"
            else -> null
        }

        val passwordError = if (password.isEmpty()) "Password is required" else null

        return LoginValidationResult(emailError = emailError, passwordError = passwordError)
    }
}
