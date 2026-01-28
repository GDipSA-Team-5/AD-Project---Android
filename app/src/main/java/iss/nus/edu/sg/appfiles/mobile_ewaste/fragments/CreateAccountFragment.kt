package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentCreateAccountBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.AuthUiState
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.RegisterEvent
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.RegisterViewModel
import kotlinx.coroutines.launch

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {
    private var binding: FragmentCreateAccountBinding? = null
    private lateinit var viewModel: RegisterViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentCreateAccountBinding.bind(view)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        observeState(fragmentBinding)

        fragmentBinding.loginLink
            .setOnClickListener { findNavController().navigate(R.id.action_createAccount_to_login) }

        val fullName = fragmentBinding.inputFullName
        val email = fragmentBinding.inputEmail
        val phone = fragmentBinding.inputPhone
        val regionId = fragmentBinding.inputRegionId
        val password = fragmentBinding.inputPassword

        fragmentBinding.buttonCreateAccount.setOnClickListener {
            val emailValue = email.text?.toString()?.trim().orEmpty()

            val valid = validateRequired(fullName, "Full name is required") &&
                validateRequired(email, "Email is required") &&
                validateEmail(email, emailValue) &&
                validateRequired(phone, "Phone is required") &&
                validateOptionalInt(regionId, "Region ID must be a number") &&
                validateRequired(password, "Password is required")

            if (valid) {
                val regionIdValue = regionId.text?.toString()?.trim().orEmpty()
                val parsedRegionId = regionIdValue.takeIf { it.isNotEmpty() }?.toIntOrNull()
                val request = RegisterRequest(
                    fullName = fullName.text.toString().trim(),
                    email = emailValue,
                    phone = phone.text.toString().trim(),
                    password = password.text.toString(),
                    regionId = parsedRegionId
                )
                viewModel.register(request)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun observeState(fragmentBinding: FragmentCreateAccountBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        renderState(fragmentBinding, state)
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is RegisterEvent.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                findNavController().navigate(R.id.action_createAccount_to_login)
                            }
                            is RegisterEvent.Error ->
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun renderState(fragmentBinding: FragmentCreateAccountBinding, state: AuthUiState) {
        fragmentBinding.buttonCreateAccount.isEnabled = !state.isLoading
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

    private fun validateOptionalInt(input: EditText, message: String): Boolean {
        val text = input.text?.toString()?.trim().orEmpty()
        return if (text.isNotEmpty() && text.toIntOrNull() == null) {
            input.error = message
            false
        } else {
            input.error = null
            true
        }
    }
}
