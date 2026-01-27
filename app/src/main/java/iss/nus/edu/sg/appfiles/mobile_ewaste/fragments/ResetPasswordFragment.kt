package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentResetPasswordBinding

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {
    private var binding: FragmentResetPasswordBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentResetPasswordBinding.bind(view)
        binding = fragmentBinding

        fragmentBinding.backToLogin
            .setOnClickListener { findNavController().navigate(R.id.action_resetPassword_to_login) }

        val emailInput = fragmentBinding.resetEmail
        fragmentBinding.buttonReset.setOnClickListener {
            val email = emailInput.text?.toString()?.trim().orEmpty()
            val valid = validateRequired(emailInput, "Email is required") &&
                validateEmail(emailInput, email)

            if (valid) {
                Toast.makeText(requireContext(), "Reset link sent", Toast.LENGTH_SHORT).show()
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