package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter.DisposalHistoryAdapter
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentDisposalHistoryBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.util.Locale


class DisposalHistoryFragment : Fragment(R.layout.fragment_disposal_history) {
    private var _binding: FragmentDisposalHistoryBinding? = null
    private val binding get() = _binding!!
    private val adapter = DisposalHistoryAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDisposalHistoryBinding.bind(view)
        binding.historyList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyList.adapter = adapter

        loadHistory("all")

        binding.chipAll.setOnClickListener { loadHistory("all") }
        binding.chipThisMonth.setOnClickListener { loadHistory("month") }
        binding.chipLast3.setOnClickListener { loadHistory("last3") }
    }

    private fun loadHistory(range: String) {
        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            toast("Please login first")
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val data = ApiClient.ewasteApi.getDisposalHistory(userId = userId, range = range)
                adapter.submitList(data)

                val totalWeight = data.sumOf { it.estimatedTotalWeight }
                val totalCount = data.size

                binding.totalWeightValue.text =
                    String.format(Locale.getDefault(), "%.2f kg", totalWeight)
                binding.totalDisposalsValue.text = totalCount.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Failed to load history: ${e.message}")
            }
        }
    }

    private fun toast(msg:String){
        val ctx = context ?: return
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
