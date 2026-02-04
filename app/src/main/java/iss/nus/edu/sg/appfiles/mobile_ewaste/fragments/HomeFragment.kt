package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter.DisposalHistoryAdapter
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentHomeBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentHomeBinding.bind(view)
        binding = fragmentBinding
        setupUsername(fragmentBinding)
        setupLogout(fragmentBinding)
        setupQuickActions(fragmentBinding)
        setupRewardsPreview(fragmentBinding)
        setupRecentHistory(fragmentBinding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun setupLogout(fragmentBinding: FragmentHomeBinding) {
        fragmentBinding.buttonLogout.setOnClickListener {
            SessionManager(requireContext()).clear()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_home_to_login)
        }
    }
    private fun setupUsername(fragmentBinding: FragmentHomeBinding){
        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            fragmentBinding.homeTitle.text = "Invalid"
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                ApiClient.ewasteApi.getUser(userId)
            }.onSuccess { user ->
                fragmentBinding.homeTitle.text = "Hello  ${user.userName}"
            }.onFailure {
                fragmentBinding.textHomeAvailablePoints.text = "0"
            }
        }
    }

    private fun setupQuickActions(fragmentBinding: FragmentHomeBinding) {
        fragmentBinding.quickActionFindBins.setOnClickListener {
            findNavController().navigate(R.id.locateFragment)
        }
        fragmentBinding.quickActionLogDisposal.setOnClickListener {
            findNavController().navigate(R.id.disposeFragment)
        }
        fragmentBinding.quickActionGuidelines.setOnClickListener {
            findNavController().navigate(R.id.guidelinesFragment)
        }
        fragmentBinding.quickActionRedeem.setOnClickListener {
            findNavController().navigate(R.id.rewardsFragment)
        }
    }

    private fun setupRewardsPreview(fragmentBinding: FragmentHomeBinding) {
        fragmentBinding.buttonHomeBrowseRewards.setOnClickListener {
            findNavController().navigate(R.id.rewardsFragment)
        }
        fragmentBinding.rewardCard.setOnClickListener {
            findNavController().navigate(R.id.rewardsFragment)
        }

        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            fragmentBinding.textHomeAvailablePoints.text = "0"
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                ApiClient.ewasteApi.getRewardsSummary(userId)
            }.onSuccess { summary ->
                fragmentBinding.textHomeAvailablePoints.text =
                    NumberFormat.getNumberInstance(Locale.getDefault()).format(summary.totalPoints)
            }.onFailure {
                fragmentBinding.textHomeAvailablePoints.text = "0"
            }
        }
    }

    private fun setupRecentHistory(fragmentBinding: FragmentHomeBinding) {
        fragmentBinding.recentSeeAll.setOnClickListener {
            findNavController().navigate(R.id.historyFragment)
        }

        val adapter = DisposalHistoryAdapter()
        fragmentBinding.recentHistoryList.layoutManager = LinearLayoutManager(requireContext())
        fragmentBinding.recentHistoryList.adapter = adapter

        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            fragmentBinding.recentEmpty.visibility = View.VISIBLE
            fragmentBinding.recentHistoryList.visibility = View.GONE
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                ApiClient.ewasteApi.getDisposalHistory(userId = userId, range = "all")
            }.onSuccess { list ->
                val preview = list.take(2)
                if (preview.isEmpty()) {
                    fragmentBinding.recentEmpty.visibility = View.VISIBLE
                    fragmentBinding.recentHistoryList.visibility = View.GONE
                } else {
                    fragmentBinding.recentEmpty.visibility = View.GONE
                    fragmentBinding.recentHistoryList.visibility = View.VISIBLE
                    adapter.submitList(preview)
                }
            }.onFailure {
                fragmentBinding.recentEmpty.visibility = View.VISIBLE
                fragmentBinding.recentHistoryList.visibility = View.GONE
            }
        }
    }
}
