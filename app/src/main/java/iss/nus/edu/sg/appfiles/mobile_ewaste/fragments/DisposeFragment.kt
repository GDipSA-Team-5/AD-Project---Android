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

    private var selectedBinId: Int? = null
    private var selectedBinLabel: String? = null

    private var categories: List<CategoryDto> = emptyList()
    private var itemTypes: List<ItemTypeDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDisposeBinding.bind(view)

        setupTimestamp()
        setupButtons()
        setupBinResultListener()

        applySelectedBinFromArgs()
        updateBinButtonText()

        loadCategories()
        resetItemTypeSpinner()
    }


    private fun setupTimestamp() {
        val fmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        binding.etTimestamp.setText(fmt.format(Date()))
    }

    private fun setupButtons() {
        binding.btnSelectBin.setOnClickListener {
            findNavController().navigate(R.id.locateFragment)
        }
        binding.buttonLogDisposal.setOnClickListener { submit() }
    }

    private fun loadCategories() {
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                categories = ApiClient.ewasteApi.getCategories()
                setupCategorySpinner()
            } catch (e: Exception) {
                toast("Failed to load categories")
            } finally {
                setLoading(false)
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

        val binId = selectedBinId
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
        selectedBinId = null
        selectedBinLabel = null
        updateBinButtonText()
        binding.spCategory.setSelection(0)
        binding.etSerialNo.setText("")
        binding.etEstimatedWeight.setText("")
        binding.etFeedback.setText("")
        resetItemTypeSpinner()
    }

    private fun setLoading(loading: Boolean) {
        binding.buttonLogDisposal.isEnabled = !loading
    }

    private fun setupBinResultListener() {
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle

        savedStateHandle?.getLiveData<Int>("selectedBinId")
            ?.observe(viewLifecycleOwner) { id ->
                if (id > 0) {
                    selectedBinId = id
                    selectedBinLabel = savedStateHandle.get<String>("selectedBinLabel")
                    updateBinButtonText()
                }
            }

        savedStateHandle?.getLiveData<String>("selectedBinLabel")
            ?.observe(viewLifecycleOwner) { label ->
                if (!label.isNullOrBlank()) {
                    selectedBinLabel = label
                    updateBinButtonText()
                }
            }
    }

    private fun applySelectedBinFromArgs() {
        val argBinId = arguments?.getInt("selectedBinId")?.takeIf { it > 0 }
        val argBinLabel = arguments?.getString("selectedBinLabel")?.trim().orEmpty()

        if (argBinId != null) {
            selectedBinId = argBinId
        }
        if (argBinLabel.isNotBlank()) {
            selectedBinLabel = argBinLabel
        }
    }

    private fun updateBinButtonText() {
        val label = selectedBinLabel?.takeIf { it.isNotBlank() }
        val id = selectedBinId
        binding.btnSelectBin.text = if (id != null && label != null) {
            "Bin $id - $label"
        } else if (id != null) {
            "Bin $id"
        } else {
            "Select bin"
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
