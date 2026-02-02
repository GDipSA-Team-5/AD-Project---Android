package iss.nus.edu.sg.appfiles.mobile_ewaste.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.graphics.Color
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import iss.nus.edu.sg.appfiles.mobile_ewaste.R
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.BinLocation
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.FragmentLocateBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.databinding.ItemNearbyBinBinding
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.locate.LocateEvent
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.locate.LocateUiState
import iss.nus.edu.sg.appfiles.mobile_ewaste.ui.locate.LocateViewModel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class LocateFragment : Fragment(R.layout.fragment_locate) {
    private var binding: FragmentLocateBinding? = null
    private var mapView: MapView? = null
    private var latestBins: List<BinLocation> = emptyList()
    private var latestUserLocation: GeoPoint? = null
    private lateinit var viewModel: LocateViewModel

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.loadCurrentLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Location permission denied. Showing default area.",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.loadDefaultLocation()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentBinding = FragmentLocateBinding.bind(view)
        binding = fragmentBinding
        setupMap(fragmentBinding)
        setupSearch(fragmentBinding)

        viewModel = ViewModelProvider(this)[LocateViewModel::class.java]
        observeState()

        requestLocationThenLoad()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDetach()
        mapView = null
        binding = null
    }

    private fun requestLocationThenLoad() {
        val permissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            viewModel.loadCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        renderState(state)
                    }
                }
                launch {
                    viewModel.events.collect { event ->
                        when (event) {
                            is LocateEvent.Error ->
                                Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun renderState(state: LocateUiState) {
        latestUserLocation = GeoPoint(state.center.latitude, state.center.longitude)
        if (state.isLoading) {
            setLoading(true)
        } else {
            setLoading(false)
            if (state.bins.isEmpty()) {
                showEmptyState()
            } else {
                renderBins(state.bins)
            }
        }
        updateMapMarkers()
    }

    private fun renderBins(bins: List<BinLocation>) {
        val fragmentBinding = binding ?: return
        fragmentBinding.nearbyBinsContainer.removeAllViews()
        fragmentBinding.nearbyBinsTitle.text = "Nearby Bins (${bins.size})"
        latestBins = bins

        fragmentBinding.nearbyBinsLoading.visibility = View.GONE
        fragmentBinding.nearbyBinsEmpty.visibility = View.GONE

        val inflater = LayoutInflater.from(requireContext())
        bins.forEach { bin ->
            val itemBinding = ItemNearbyBinBinding.inflate(
                inflater,
                fragmentBinding.nearbyBinsContainer,
                false
            )
            itemBinding.binName.text = bin.name
            itemBinding.binAddress.text = bin.address
            itemBinding.binAccess.text = bin.access
            itemBinding.binDistance.text = formatDistance(bin.distanceMeters)
            itemBinding.binDescription.text = bin.description
            val isMaintenance = bin.access.equals("Maintenance", ignoreCase = true)
            if (isMaintenance) {
                itemBinding.binAccess.setTextColor(Color.parseColor("#DC2626"))
            } else {
                itemBinding.binAccess.setTextColor(Color.parseColor("#2FAE66"))
            }
            itemBinding.binSelectButton.isEnabled = !isMaintenance
            if (isMaintenance) {
                itemBinding.binSelectButton.alpha = 0.5f
                itemBinding.binCard.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
            } else {
                itemBinding.binSelectButton.alpha = 1f
                itemBinding.binCard.setCardBackgroundColor(Color.WHITE)
            }
            itemBinding.binSelectButton.setOnClickListener {
                val navController = findNavController()
                val previousEntry = navController.previousBackStackEntry

                // If user came here from Dispose, return the selection instead of
                // pushing a new Dispose instance onto the back stack.
                if (previousEntry?.destination?.id == R.id.disposeFragment) {
                    previousEntry.savedStateHandle["selectedBinId"] = bin.binId
                    previousEntry.savedStateHandle["selectedBinLabel"] = bin.name
                    navController.navigateUp()
                    return@setOnClickListener
                }

                // Otherwise (e.g., entered Locate via bottom nav), navigate to Dispose.
                val args = Bundle().apply {
                    putInt("selectedBinId", bin.binId)
                    putString("selectedBinLabel", bin.name)
                }
                navController.navigate(R.id.action_locate_to_dispose, args)
            }
            fragmentBinding.nearbyBinsContainer.addView(itemBinding.root)
        }
    }

    private fun showEmptyState() {
        val fragmentBinding = binding ?: return
        fragmentBinding.nearbyBinsContainer.removeAllViews()
        fragmentBinding.nearbyBinsLoading.visibility = View.GONE
        fragmentBinding.nearbyBinsEmpty.visibility = View.VISIBLE
        fragmentBinding.nearbyBinsTitle.text = "Nearby Bins (0)"
        latestBins = emptyList()
    }

    private fun setLoading(loading: Boolean) {
        val fragmentBinding = binding ?: return
        fragmentBinding.nearbyBinsLoading.visibility = if (loading) View.VISIBLE else View.GONE
        fragmentBinding.nearbyBinsEmpty.visibility = View.GONE
    }

    private fun setupMap(fragmentBinding: FragmentLocateBinding) {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osm", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName
        mapView = fragmentBinding.osmMap
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)
        mapView?.controller?.setZoom(11.0)
        mapView?.controller?.setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LNG))
        updateMapMarkers()
    }

    private fun setupSearch(fragmentBinding: FragmentLocateBinding) {
        fragmentBinding.searchQuery.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handlePostcodeSearch(fragmentBinding)
                true
            } else {
                false
            }
        }

        fragmentBinding.searchButton.setOnClickListener {
            handlePostcodeSearch(fragmentBinding)
        }
    }

    private fun handlePostcodeSearch(fragmentBinding: FragmentLocateBinding) {
        val query = fragmentBinding.searchQuery.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a postcode.", Toast.LENGTH_SHORT).show()
            return
        }
        if (query.length != 6) {
            Toast.makeText(requireContext(), "Postcode must be 6 digits.", Toast.LENGTH_SHORT).show()
            return
        }

        hideKeyboard(fragmentBinding.searchQuery)
        viewModel.searchByPostcode(query)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateMapMarkers() {
        val map = mapView ?: return
        map.overlays.clear()

        val userLocation = latestUserLocation
        if (userLocation != null) {
            val userMarker = Marker(map)
            userMarker.position = userLocation
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            userMarker.title = "You are here"
            map.overlays.add(userMarker)
        }

        latestBins.forEach { bin ->
            val marker = Marker(map)
            marker.position = GeoPoint(bin.coordinate.latitude, bin.coordinate.longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = bin.name
            marker.subDescription = bin.address
            map.overlays.add(marker)
        }

        val points = mutableListOf<GeoPoint>()
        if (userLocation != null) points.add(userLocation)
        latestBins.forEach { bin -> points.add(GeoPoint(bin.coordinate.latitude, bin.coordinate.longitude)) }

        if (points.isNotEmpty()) {
            val bbox = BoundingBox.fromGeoPoints(points)
            map.zoomToBoundingBox(bbox, true, 80)
        } else {
            map.controller.setZoom(11.0)
            map.controller.setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LNG))
        }

        map.invalidate()
    }

    private fun formatDistance(distanceMeters: Float): String {
        return if (distanceMeters < 1000f) {
            String.format(Locale.US, "%.0f m", distanceMeters)
        } else {
            String.format(Locale.US, "%.1f km", distanceMeters / 1000f)
        }
    }

    companion object {
        private const val DEFAULT_LAT = 1.2966
        private const val DEFAULT_LNG = 103.7764
    }
}
