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
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentLoginBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.AuthUiState
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.LoginEvent
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.LoginViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var binding: FragmentLoginBinding? = null
    private lateinit var viewModel: LoginViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentLoginBinding.bind(view)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        observeState(fragmentBinding)

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
                viewModel.login(email, password)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun observeState(fragmentBinding: FragmentLoginBinding) {
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
                            is LoginEvent.Success -> {
                                SessionManager(requireContext()).saveLogin(event.userId)
                                Toast.makeText(requireContext(), "Logged in", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_login_to_home)
                            }
                            is LoginEvent.Error ->
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun renderState(fragmentBinding: FragmentLoginBinding, state: AuthUiState) {
        fragmentBinding.buttonLogin.isEnabled = !state.isLoading
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
