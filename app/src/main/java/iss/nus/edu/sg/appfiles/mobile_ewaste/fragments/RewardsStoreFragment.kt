package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter.RewardsCatalogueAdapter
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.DTOs.RedeemRequestDto
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentRewardsStoreBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class RewardsStoreFragment : Fragment(R.layout.fragment_rewards_store) {

    private var _binding: FragmentRewardsStoreBinding? = null
    private val binding get() = _binding!!

    private var availablePoints: Int = 0

    private val adapter = RewardsCatalogueAdapter { reward ->
        redeemReward(reward.rewardId, reward.rewardName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRewardsStoreBinding.bind(view)

        binding.rewardsStoreList.layoutManager = LinearLayoutManager(requireContext())
        binding.rewardsStoreList.adapter = adapter

        loadRewardsStore()
    }

    private fun loadRewardsStore() {
        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            toast("Please login first")
            return
        }

        binding.rewardsStoreLoading.visibility = View.VISIBLE
        binding.rewardsStoreEmpty.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val wallet = ApiClient.ewasteApi.getRewardWallet(userId)
                availablePoints = wallet.availablePoints
                val formattedPoints = NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(availablePoints)
                binding.rewardsStoreSubtitle.text = "$formattedPoints points available"
                adapter.updateAvailablePoints(availablePoints)

                val rewards = ApiClient.ewasteApi.getRewardCatalogue()
                if (rewards.isEmpty()) {
                    binding.rewardsStoreEmpty.visibility = View.VISIBLE
                }
                adapter.submitList(rewards)
            } catch (ex: Exception) {
                toast("Failed to load rewards store")
                binding.rewardsStoreEmpty.visibility = View.VISIBLE
            } finally {
                binding.rewardsStoreLoading.visibility = View.GONE
            }
        }
    }

    private fun redeemReward(rewardId: Int, rewardName: String) {
        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            toast("Please login first")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.ewasteApi.redeemReward(
                    RedeemRequestDto(userId = userId, rewardId = rewardId)
                )
                if (response.success) {
                    toast("Redeemed $rewardName")
                    loadRewardsStore()
                } else {
                    toast(response.message)
                }
            } catch (ex: Exception) {
                toast("Redeem failed")
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
