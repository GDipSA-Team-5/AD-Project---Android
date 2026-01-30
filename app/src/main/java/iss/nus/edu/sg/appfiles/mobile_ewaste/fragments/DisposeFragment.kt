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
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.BinDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CategoryDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.CreateDisposalLogRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.ItemTypeDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentDisposeBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DisposeFragment : Fragment(R.layout.fragment_dispose) {
    private var _binding: FragmentDisposeBinding? = null
    private val binding get() = _binding!!

    private var bins: List<BinDto> = emptyList()
    private var categories: List<CategoryDto> = emptyList()
    private var itemTypes: List<ItemTypeDto> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDisposeBinding.bind(view)
        setupTimestamp()
        setupClicks()
        loadBinsAndCategories()
    }

    private fun setupTimestamp(){
        val fmt = SimpleDateFormat("dd MMM yyyy, HH:mm",Locale.getDefault())
        binding.etTimestamp.setText(fmt.format(Date()))
    }

    private fun setupClicks(){
        binding.btnBack.setOnClickListener { goBack() }
        binding.buttonLogDisposal.setOnClickListener { submit() }
    }

    private fun loadBinsAndCategories(){
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try{
                bins = ApiClient.ewasteApi.getBins()
                categories = ApiClient.ewasteApi.getCategories()

                val binDisplayList: List<Any> = listOf("Select bin") + bins

                binding.spBin.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    binDisplayList
                )
                val categoryDisplayList: List<Any> = listOf("Select category") + categories

                binding.spCategory.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryDisplayList
                )

                binding.spItemType.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Select item type")
                )

                binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if(position==0){
                            itemTypes = emptyList()
                            binding.spItemType.adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                listOf("Select item type")
                            )
                            return
                        }
                        val selectedCategory = categories[position-1]
                        loadItemTypes(selectedCategory.categoryId)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                binding.spItemType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, v: View?, position: Int, id: Long) {
                        if (position == 0 || itemTypes.isEmpty()) return
                        val selectedItemType = itemTypes[position - 1] // -1 because of "Select item type"

                        // Auto-fill weight (user can still edit)
                        binding.etEstimatedWeight.setText(selectedItemType.estimatedAvgWeight.toString())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                }
            }catch (e: Exception){
                toast("Failed to load bins/categories.")
            }finally {
                setLoading(false)
            }
        }
    }
    private fun loadItemTypes(categoryId: Int) {
        setLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                itemTypes = ApiClient.ewasteApi.getItemTypes(categoryId)

                val itemTypeDisplayList: List<Any> =
                    listOf("Select item type") + itemTypes

                binding.spItemType.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    itemTypeDisplayList
                )

            } catch (e: Exception) {
                itemTypes = emptyList()
                binding.spItemType.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    listOf("Select item type")
                )
                toast("Failed to load item types.")
            } finally {
                setLoading(false)
            }
        }
    }
    private fun submit() {
        // Required: category + item type
        val categoryPos = binding.spCategory.selectedItemPosition
        val itemTypePos = binding.spItemType.selectedItemPosition

        val serialNo = binding.etSerialNo.text.toString().trim()
        val weightStr = binding.etEstimatedWeight.text.toString().trim()
        val feedback = binding.etFeedback.text.toString().trim()

        if (categoryPos == 0) {
            toast("Please select a category"); return
        }
        if (itemTypePos == 0) {
            toast("Please select an item type"); return
        }
        if (serialNo.isEmpty()) {
            toast("Serial No is required"); return
        }
        if (weightStr.isEmpty()) {
            toast("Estimated weight is required"); return
        }

        val weight = weightStr.toDoubleOrNull()
        if (weight == null || weight <= 0.0) {
            toast("Estimated weight must be a number > 0"); return
        }

        val binPos = binding.spBin.selectedItemPosition
        val binId: Int? = if (binPos == 0) null else bins[binPos - 1].binId

        // Selected item type id
        val selectedItemTypeId = itemTypes[itemTypePos - 1].itemTypeId

        val req = CreateDisposalLogRequest(
            binId = binId,
            itemTypeId = selectedItemTypeId,
            serialNo = serialNo,
            estimatedWeightKg = weight,
            feedback = feedback.ifBlank { null }
        )

        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                ApiClient.ewasteApi.createDisposalLog(req)
                toast("Submitted ✅")

                clearFormKeepTimestamp()

            } catch (e: Exception) {
                toast("Submit failed. Check API connection.")
            } finally {
                setLoading(false)
            }
        }
    }
    private fun clearFormKeepTimestamp() {
        binding.spBin.setSelection(0)
        binding.etFeedback.setText("")
        binding.spCategory.setSelection(0)

        // reset item types spinner
        itemTypes = emptyList()
        binding.spItemType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Select item type")
        )

        binding.etSerialNo.setText("")
        binding.etEstimatedWeight.setText("")
    }
    private fun setLoading(loading: Boolean) {
        // Simple UX: disable submit while loading
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