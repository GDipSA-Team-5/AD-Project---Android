package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.Adapter.RewardRedemptionAdapter
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentProfileBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private var binding: FragmentProfileBinding? = null
    private val redemptionAdapter = RewardRedemptionAdapter { item ->
        onUseRedemption(item.redemptionId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentProfileBinding.bind(view)
        binding = fragmentBinding

        fragmentBinding.redeemedList.layoutManager = LinearLayoutManager(requireContext())
        fragmentBinding.redeemedList.adapter = redemptionAdapter
        val btnEdit = view.findViewById<TextView>(R.id.btnEdit)
        btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_editProfile)
        }

        setupLogout(fragmentBinding)
        loadProfile(fragmentBinding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupLogout(fragmentBinding: FragmentProfileBinding) {
        fragmentBinding.buttonProfileLogout.setOnClickListener {
            SessionManager(requireContext()).clear()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun loadProfile(fragmentBinding: FragmentProfileBinding) {
        val userId = SessionManager(requireContext()).userId()
        if (userId == null) {
            toast("Please login first")
            return
        }
        redemptionAdapter.setUserId(userId)

        fragmentBinding.redeemedLoading.visibility = View.VISIBLE
        fragmentBinding.redeemedEmpty.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val profile = ApiClient.ewasteApi.getUser(userId)
                fragmentBinding.profileName.text = profile.userName ?: "User"
                fragmentBinding.profileEmail.text = profile.email ?: "-"
                fragmentBinding.profilePhone.text = profile.phoneNumber ?: "-"

                val redemptions = ApiClient.ewasteApi.getRewardRedemptions(userId)
                if (redemptions.isEmpty()) {
                    fragmentBinding.redeemedEmpty.visibility = View.VISIBLE
                }
                redemptionAdapter.submitList(redemptions)
            } catch (_: Exception) {
                toast("Failed to load profile")
                fragmentBinding.redeemedEmpty.visibility = View.VISIBLE
            } finally {
                fragmentBinding.redeemedLoading.visibility = View.GONE
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onUseRedemption(redemptionId: Int) {
        // No-op for now; UI already shows the code.
        //ProLam will do it
    }
}
