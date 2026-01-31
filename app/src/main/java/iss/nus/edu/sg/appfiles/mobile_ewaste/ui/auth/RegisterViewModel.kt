package iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository.AuthRepository
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository.RegionRepository
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegisterRequest
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.RegionDto
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository(ApiClient.authApi)
    private val regionRepository = RegionRepository(ApiClient.regionApi)

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _regions = MutableStateFlow(RegionUiState())
    val regions: StateFlow<RegionUiState> = _regions.asStateFlow()

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadRegions()
    }

    private fun loadRegions() {
        viewModelScope.launch {
            _regions.update { it.copy(isLoading = true, error = null) }
            try {
                val list = regionRepository.getRegions()
                _regions.update { it.copy(isLoading = false, items = list) }
            } catch (ex: Exception) {
                _regions.update { it.copy(isLoading = false, error = "Failed to load regions") }
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = authRepository.register(request)
                _state.update { it.copy(isLoading = false) }
                if (response.success) {
                    _events.trySend(RegisterEvent.Success)
                } else {
                    _events.trySend(RegisterEvent.Error(response.message))
                }
            } catch (ex: Exception) {
                _state.update { it.copy(isLoading = false) }
                _events.trySend(RegisterEvent.Error("Registration failed"))
            }
        }
    }
}

data class RegionUiState(
    val isLoading: Boolean = false,
    val items: List<RegionDto> = emptyList(),
    val error: String? = null
)

sealed class RegisterEvent {
    data object Success : RegisterEvent()
    data class Error(val message: String) : RegisterEvent()
}
