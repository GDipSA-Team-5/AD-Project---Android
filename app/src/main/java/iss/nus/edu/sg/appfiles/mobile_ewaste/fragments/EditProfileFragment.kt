package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.UpdateProfileRequestDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private var regionItems: List<RegionDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSave = view.findViewById<TextView>(R.id.btnSave)
        val phoneInput = view.findViewById<EditText>(R.id.etPhone)
        val regionSpinner = view.findViewById<Spinner>(R.id.spinnerRegion)

        val session = SessionManager(requireContext())
        phoneInput.setText(session.phoneNumber().orEmpty())
        setupRegionSpinner(regionSpinner)
        loadRegionsAndProfile(regionSpinner, phoneInput)

        btnSave.setOnClickListener {
            val phone = phoneInput.text?.toString()?.trim().orEmpty()
            val selectedRegionId = getSelectedRegionId(regionSpinner)

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val profile = ApiClient.ewasteApi.updateProfile(
                        UpdateProfileRequestDto(
                            phoneNumber = phone,
                            regionId = selectedRegionId
                        )
                    )
                    session.saveProfile(profile.phoneNumber, profile.regionId, profile.regionName)
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRegionSpinner(spinner: Spinner) {
        val placeholder = listOf(getString(R.string.hint_region))
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            placeholder
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun loadRegionsAndProfile(spinner: Spinner, phoneInput: EditText) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val regions = ApiClient.ewasteApi.getRegions()
                regionItems = regions
                updateRegionSpinner(spinner, regions)

                val profile = ApiClient.ewasteApi.getUser(SessionManager(requireContext()).userId() ?: return@launch)
                phoneInput.setText(profile.phoneNumber.orEmpty())
                setSelectedRegion(spinner, profile.regionId)
            } catch (_: Exception) {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateRegionSpinner(spinner: Spinner, regions: List<RegionDto>) {
        val names = mutableListOf(getString(R.string.hint_region))
        names.addAll(regions.map { it.regionName ?: "Region ${it.regionId}" })
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            names
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setSelectedRegion(spinner: Spinner, regionId: Int?) {
        if (regionId == null) {
            spinner.setSelection(0)
            return
        }
        val index = regionItems.indexOfFirst { it.regionId == regionId }
        spinner.setSelection(if (index >= 0) index + 1 else 0)
    }

    private fun getSelectedRegionId(spinner: Spinner): Int? {
        val position = spinner.selectedItemPosition
        if (position <= 0) return null
        val index = position - 1
        return regionItems.getOrNull(index)?.regionId
    }
}
