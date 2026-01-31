package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.*
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentDisposeBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DisposeFragment : Fragment(R.layout.fragment_dispose) {

    private var _binding: FragmentDisposeBinding? = null
    private val binding get() = _binding!!

    private var bins: List<BinDto> = emptyList()
    private var categories: List<CategoryDto> = emptyList()
    private var itemTypes: List<ItemTypeDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDisposeBinding.bind(view)

        val selectedBinId = arguments?.getInt("selectedBinId")?.takeIf { it > 0 }

        setupTimestamp()
        setupButtons()
        loadInitialData(selectedBinId)
    }


    private fun setupTimestamp() {
        val fmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        binding.etTimestamp.setText(fmt.format(Date()))
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { goBack() }
        binding.buttonLogDisposal.setOnClickListener { submit() }
    }


    private fun loadInitialData(selectedBinId: Int?) {
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                bins = ApiClient.ewasteApi.getBins()
                categories = ApiClient.ewasteApi.getCategories()

                setupBinSpinner(selectedBinId)
                setupCategorySpinner()
                resetItemTypeSpinner()

                toast("Bins=${bins.size}, Categories=${categories.size}")

            } catch (e: Exception) {
                toast("Failed to load bins / categories")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setupBinSpinner(selectedBinId: Int?) {
        val labels = listOf("Select bin") + bins.map {
            "${it.binId} - ${it.locationName ?: "Bin"}"
        }

        binding.spBin.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            labels
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        if (selectedBinId != null) {
            val index = bins.indexOfFirst { it.binId == selectedBinId }
            if (index >= 0) {
                binding.spBin.setSelection(index + 1)
            }
        }
    }

    private fun setupCategorySpinner() {
        val labels = listOf("Select category") + categories.map { it.categoryName }

        binding.spCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            labels
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        resetItemTypeSpinner()
                        return
                    }
                    loadItemTypes(categories[position - 1].categoryId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun loadItemTypes(categoryId: Int) {
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                itemTypes = ApiClient.ewasteApi.getItemTypes(categoryId)

                val labels = listOf("Select item type") + itemTypes.map { it.itemName }

                binding.spItemType.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    labels
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }

                binding.spItemType.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position == 0) return
                            binding.etEstimatedWeight.setText(
                                itemTypes[position - 1].estimatedAvgWeight.toString()
                            )
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }

            } catch (e: Exception) {
                toast("Failed to load item types")
                resetItemTypeSpinner()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun resetItemTypeSpinner() {
        itemTypes = emptyList()
        binding.spItemType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Select item type")
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }


    private fun submit() {
        val categoryPos = binding.spCategory.selectedItemPosition
        val itemTypePos = binding.spItemType.selectedItemPosition

        val serialNo = binding.etSerialNo.text.toString().trim()
        val weightStr = binding.etEstimatedWeight.text.toString().trim()
        val feedback = binding.etFeedback.text.toString().trim()

        if (categoryPos == 0) return toast("Select category")
        if (itemTypePos == 0) return toast("Select item type")
        if (serialNo.isEmpty()) return toast("Serial number required")
        if (weightStr.isEmpty()) return toast("Estimated weight required")

        val weight = weightStr.toDoubleOrNull()
            ?: return toast("Invalid weight")

        val binPos = binding.spBin.selectedItemPosition
        val binId = if (binPos == 0) null else bins[binPos - 1].binId
        val itemTypeId = itemTypes[itemTypePos - 1].itemTypeId
        // Get userId from SessionManager
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.userId() ?: run {
            toast("Please login first")
            return
        }

        val request = CreateDisposalLogRequest(
            binId = binId,
            itemTypeId = itemTypeId,
            serialNo = serialNo,
            estimatedWeightKg = weight,
            feedback = feedback.ifBlank { null },
            userId = userId
        )


        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ApiClient.ewasteApi.createDisposalLog(request)
                toast("Submitted successfully")
                clearFormKeepTimestamp()
            } catch (e: Exception) {
                toast("Submit failed")
            } finally {
                setLoading(false)
            }
        }
    }


    private fun clearFormKeepTimestamp() {
        binding.spBin.setSelection(0)
        binding.spCategory.setSelection(0)
        binding.etSerialNo.setText("")
        binding.etEstimatedWeight.setText("")
        binding.etFeedback.setText("")
        resetItemTypeSpinner()
    }

    private fun setLoading(loading: Boolean) {
        binding.buttonLogDisposal.isEnabled = !loading
        binding.btnBack.isEnabled = !loading
    }

    private fun goBack() {
        runCatching { findNavController().navigateUp() }
            .onFailure { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
