package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEdit = view.findViewById<TextView>(R.id.btnEdit)

        btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_editProfile)
        }
    }
}

