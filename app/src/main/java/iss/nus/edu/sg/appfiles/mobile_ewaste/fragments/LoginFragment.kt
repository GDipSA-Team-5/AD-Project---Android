package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentLoginBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginRequest
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var binding: FragmentLoginBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentLoginBinding.bind(view)
        binding = fragmentBinding

        fragmentBinding.forgotPassword
            .setOnClickListener { findNavController().navigate(R.id.action_login_to_resetPassword) }
        fragmentBinding.registerLink
            .setOnClickListener { findNavController().navigate(R.id.action_login_to_createAccount) }

        val emailInput = fragmentBinding.loginEmail
        val passwordInput = fragmentBinding.loginPassword
        fragmentBinding.buttonLogin.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val password = passwordInput.text?.toString().orEmpty()

            val emailValid = validateRequired(emailInput, "Email is required") &&
                validateEmail(emailInput, email)
            val passwordValid = validateRequired(passwordInput, "Password is required")

            if (emailValid && passwordValid) {
                val request = LoginRequest(email = email, password = password)
                lifecycleScope.launch {
                    try {
                        val response = ApiClient.authApi.login(request)
                        if (response.success) {
                            SessionManager(requireContext()).saveLogin(response.userId)
                            Toast.makeText(
                                requireContext(),
                                "Logged in",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_login_to_home)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                response.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (ex: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "Login failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun validateRequired(input: EditText, message: String): Boolean {
        val text = input.text?.toString()?.trim().orEmpty()
        return if (text.isEmpty()) {
            input.error = message
            false
        } else {
            input.error = null
            true
        }
    }

    private fun validateEmail(input: EditText, value: String): Boolean {
        return if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            input.error = "Invalid email"
            false
        } else {
            input.error = null
            true
        }
    }
}
