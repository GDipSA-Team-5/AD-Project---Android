package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentLoginBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.AuthUiState
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.LoginFormValidator
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

            val validation = LoginFormValidator.validate(email, password)
            emailInput.error = validation.emailError
            passwordInput.error = validation.passwordError

            if (validation.isValid) {
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
                                val options = NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true) // inclusive = true
                                    .build()
                                findNavController().navigate(R.id.homeFragment, null, options)
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

}
