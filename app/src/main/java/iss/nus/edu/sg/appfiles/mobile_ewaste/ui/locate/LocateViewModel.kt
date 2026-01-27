package iss.nus.edu.sg.appfiles.mobile_ewaste.ui.locate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.BinLocation
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.model.Coordinate
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository.EwasteRepository
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository.GeocodingRepository
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository.LocationRepository
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.DataGovApiClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocateViewModel(application: Application) : AndroidViewModel(application) {
    private val ewasteRepository = EwasteRepository(
        application.applicationContext,
        DataGovApiClient.dataGovApi
    )
    private val locationRepository = LocationRepository(application.applicationContext)
    private val geocodingRepository = GeocodingRepository(application.applicationContext)

    private val _state = MutableStateFlow(LocateUiState())
    val state: StateFlow<LocateUiState> = _state.asStateFlow()

    private val _events = Channel<LocateEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun loadCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val coordinate = locationRepository.getBestLocation() ?: DEFAULT_COORDINATE
            loadBinsForCoordinate(coordinate)
        }
    }

    fun loadDefaultLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loadBinsForCoordinate(DEFAULT_COORDINATE)
        }
    }

    fun searchByPostcode(postcode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val coordinate = geocodingRepository.geocodePostcode(postcode)
            if (coordinate == null) {
                _state.update { it.copy(isLoading = false) }
                _events.trySend(LocateEvent.Error("Postcode not found."))
                return@launch
            }
            loadBinsForCoordinate(coordinate)
        }
    }

    private suspend fun loadBinsForCoordinate(coordinate: Coordinate) {
        try {
            val bins = ewasteRepository.getNearbyBins(coordinate, limit = 3)
            _state.update {
                it.copy(
                    isLoading = false,
                    bins = bins,
                    center = coordinate
                )
            }
            if (bins.isEmpty()) {
                _events.trySend(LocateEvent.Error("No nearby bins found."))
            }
        } catch (ex: Exception) {
            _state.update { it.copy(isLoading = false) }
            _events.trySend(LocateEvent.Error("Unable to load bins."))
        }
    }

    companion object {
        val DEFAULT_COORDINATE = Coordinate(latitude = 1.3521, longitude = 103.8198)
    }
}

data class LocateUiState(
    val isLoading: Boolean = false,
    val bins: List<BinLocation> = emptyList(),
    val center: Coordinate = LocateViewModel.DEFAULT_COORDINATE
)

sealed class LocateEvent {
    data class Error(val message: String) : LocateEvent()
}
