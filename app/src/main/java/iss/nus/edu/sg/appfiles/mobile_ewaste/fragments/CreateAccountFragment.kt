package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
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
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.AuthUiState
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.RegisterEvent
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth.RegisterViewModel
import kotlinx.coroutines.launch

class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {
    private var binding: FragmentCreateAccountBinding? = null
    private lateinit var viewModel: RegisterViewModel
    private var regionItems: List<RegionDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentCreateAccountBinding.bind(view)
        binding = fragmentBinding
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        fragmentBinding.loginLink
            .setOnClickListener { findNavController().navigate(R.id.action_createAccount_to_login) }

        val fullName = fragmentBinding.inputFullName
        val email = fragmentBinding.inputEmail
        val phone = fragmentBinding.inputPhone
        val regionId = fragmentBinding.inputRegionId
        val password = fragmentBinding.inputPassword

        setupRegionSpinner(regionId)
        observeState(fragmentBinding, regionId)

        fragmentBinding.buttonCreateAccount.setOnClickListener {
            val emailValue = email.text?.toString()?.trim().orEmpty()

            val valid = validateRequired(fullName, "Full name is required") &&
                validateRequired(email, "Email is required") &&
                validateEmail(email, emailValue) &&
                validateRequired(phone, "Phone is required") &&
                validateRequired(password, "Password is required")

            if (valid) {
                val selectedRegionId = getSelectedRegionId(regionId)
                val request = RegisterRequest(
                    fullName = fullName.text.toString().trim(),
                    email = emailValue,
                    phone = phone.text.toString().trim(),
                    password = password.text.toString(),
                    regionId = selectedRegionId
                )
                viewModel.register(request)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun observeState(fragmentBinding: FragmentCreateAccountBinding, regionSpinner: Spinner) {
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
                launch {
                    viewModel.regions.collect { regionState ->
                        if (regionState.items.isNotEmpty()) {
                            regionItems = regionState.items
                            updateRegionSpinner(regionSpinner, regionItems)
                        }
                        regionState.error?.let { message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    private fun setupRegionSpinner(spinner: Spinner) {
        val placeholder = listOf("No region")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            placeholder
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun updateRegionSpinner(spinner: Spinner, regions: List<RegionDto>) {
        val names = mutableListOf("No region")
        names.addAll(regions.map { it.regionName ?: "Region ${it.regionId}" })
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            names
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun getSelectedRegionId(spinner: Spinner): Int? {
        val position = spinner.selectedItemPosition
        if (position <= 0) return null
        val index = position - 1
        return regionItems.getOrNull(index)?.regionId
    }
}
