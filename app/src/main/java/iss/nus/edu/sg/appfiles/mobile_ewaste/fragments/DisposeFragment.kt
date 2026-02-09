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
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CategoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CreateDisposalLogRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.ItemTypeDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentDisposeBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        val args = DisposeFragmentArgs.fromBundle(requireArguments())

        if (args.selectedBinId > 0) {
            selectedBinId = args.selectedBinId
            selectedBinLabel = args.selectedBinLabel
            updateBinButtonText()
        }
        setupTimestamp()
        setupButtons()
        applySelectedBinFromArgs()
        loadCategories()
        resetItemTypeSpinner()
    }

    private fun updateBinButtonText() {
        binding.btnSelectBin.text = selectedBinLabel?:"Bin #$selectedBinId"}

    private fun setupTimestamp() {
        val fmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        binding.etTimestamp.setText(fmt.format(Date()))
    }

    private fun setupButtons() {
        binding.btnSelectBin.setOnClickListener {
            findNavController().navigate(R.id.qrScanFragment)
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

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Intentionally left empty:
                    // This callback is required by OnItemSelectedListener,
                    // but our UI always has a default selection,
                    // so this state will never be triggered.
                }
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

                        override fun onNothingSelected(parent: AdapterView<*>?)
                            {
                                // Intentionally left empty:
                                // This callback is required by OnItemSelectedListener,
                                // but our UI always has a default selection,
                                // so this state will never be triggered.
                            }

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

        val validationError = DisposeFormValidator.validate(
            DisposeFormInput(
                categoryPosition = categoryPos,
                itemTypePosition = itemTypePos,
                serialNo = serialNo,
                weightText = weightStr
            )
        )
        if (validationError != null) return toast(validationError)

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


    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
