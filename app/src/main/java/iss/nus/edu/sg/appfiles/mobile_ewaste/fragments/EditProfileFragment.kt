package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSave = view.findViewById<TextView>(R.id.btnSave)

        btnSave.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
