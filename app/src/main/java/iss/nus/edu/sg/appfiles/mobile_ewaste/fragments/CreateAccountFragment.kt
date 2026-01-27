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
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentCreateAccountBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterRequest
import kotlinx.coroutines.launch

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {
    private var binding: FragmentCreateAccountBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentCreateAccountBinding.bind(view)
        binding = fragmentBinding

        fragmentBinding.loginLink
            .setOnClickListener { findNavController().navigate(R.id.action_createAccount_to_login) }

        val fullName = fragmentBinding.inputFullName
        val email = fragmentBinding.inputEmail
        val phone = fragmentBinding.inputPhone
        val address = fragmentBinding.inputAddress
        val password = fragmentBinding.inputPassword
        val referral = fragmentBinding.inputReferral

        fragmentBinding.buttonCreateAccount.setOnClickListener {
            val emailValue = email.text?.toString()?.trim().orEmpty()
            val referralValue = referral.text?.toString()?.trim().orEmpty().ifEmpty { null }

            val valid = validateRequired(fullName, "Full name is required") &&
                validateRequired(email, "Email is required") &&
                validateEmail(email, emailValue) &&
                validateRequired(phone, "Phone is required") &&
                validateRequired(address, "Address is required") &&
                validateRequired(password, "Password is required")

            if (valid) {
                val request = RegisterRequest(
                    fullName = fullName.text.toString().trim(),
                    email = emailValue,
                    phone = phone.text.toString().trim(),
                    address = address.text.toString().trim(),
                    password = password.text.toString(),
                    referralCode = referralValue
                )

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.authApi.register(request)
                        if (response.success) {
                            Toast.makeText(
                                requireContext(),
                                "Registered successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigate(R.id.action_createAccount_to_login)
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
                            "Registration failed",
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
