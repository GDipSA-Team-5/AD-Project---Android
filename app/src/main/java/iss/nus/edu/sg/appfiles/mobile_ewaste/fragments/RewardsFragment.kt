package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter.RewardsHistoryAdapter
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentRewardsBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class RewardsFragment : Fragment(R.layout.fragment_rewards) {

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    private val adapter = RewardsHistoryAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRewardsBinding.bind(view)

        binding.rewardsHistoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rewardsHistoryList.adapter = adapter

        // Defaults so UI isn't blank while loading
        binding.textAvailablePoints.text = "0"

        loadRewards()

        binding.buttonRedeem.setOnClickListener {
            toast("Redeem (later)")
        }
    }

    private fun loadRewards() {
        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            toast("Please login first")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // ✅ summary endpoint
                val summary = ApiClient.ewasteApi.getRewardsSummary(userId)

                binding.textAvailablePoints.text =
                    NumberFormat.getNumberInstance(Locale.getDefault())
                        .format(summary.totalPoints)

                binding.textTotalDisposals.text = summary.totalDisposals.toString()
                binding.textTotalRedeemed.text = summary.totalRedeemed.toString()
                binding.textTotalReferrals.text = summary.totalReferrals.toString()

                val history = ApiClient.ewasteApi.getRewardsHistory(userId)

                adapter.submitList(history)

            } catch (e: Exception) {
                e.printStackTrace()
                toast("Failed to load rewards: ${e.message}")
            }
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
