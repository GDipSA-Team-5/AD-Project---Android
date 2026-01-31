package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentHomeBinding.bind(view)
        binding = fragmentBinding

        setupLogout(fragmentBinding)
        setupQuickActions(fragmentBinding)
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
}
