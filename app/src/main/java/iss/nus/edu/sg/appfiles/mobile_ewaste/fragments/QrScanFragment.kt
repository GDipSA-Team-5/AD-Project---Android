package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentQrScanBinding


class QrScanFragment : Fragment(R.layout.fragment_qr_scan) {
    private var _binding : FragmentQrScanBinding? = null
    private val binding get() = _binding!!
    private var handled = false

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            startScanner()
        } else {
            toast("Camera permission not granted")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQrScanBinding.bind(view)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startScanner()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

    }
        private fun extractBinId(raw: String):Int? {
            raw.toIntOrNull()?.let{if (it > 0) return it}

            val match = Regex("""(\d{1,9})""").find(raw)?: return null
            val id = match.value.toIntOrNull()?: return null
            return if (id>0) id else null
        }
    private fun startScanner() {
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                if (result == null || handled) return

                val raw = result.text ?: return
                val binId = extractBinId(raw) ?: return

                handled = true
                binding.barcodeScanner.pause()

                val action =
                    QrScanFragmentDirections.actionQrScanFragmentToDisposeFragment(
                        selectedBinId = binId,
                        selectedBinLabel = "Bin #$binId"
                    )

                findNavController().navigate(action)
            }
        })
    }
    override fun onResume(){
        super.onResume()
        binding.barcodeScanner.resume()
    }
    override fun onPause(){
        super.onPause()
        binding.barcodeScanner.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun toast(msg:String){
        Toast.makeText(requireContext(),msg,Toast.LENGTH_SHORT).show()
    }
}