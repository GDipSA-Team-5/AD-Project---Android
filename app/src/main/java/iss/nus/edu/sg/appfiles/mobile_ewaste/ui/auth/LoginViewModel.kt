package iss.nus.edu.sg.appfiles.mobile_ewaste.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.repository.AuthRepository
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.ApiClient
import iss.nus.edu.sg.appfiles.mobile_ewaste.network.model.LoginRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository(ApiClient.ewasteApi)

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val response = authRepository.login(LoginRequest(email = email, password = password))
                _state.update { it.copy(isLoading = false) }
                if (response.success) {
                    _events.trySend(LoginEvent.Success(response.userId, response.token))
                } else {
                    _events.trySend(LoginEvent.Error(response.message))
                }
            } catch (ex: Exception) {
                _state.update { it.copy(isLoading = false) }
                _events.trySend(LoginEvent.Error("Login failed"))
            }
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    data class Success(val userId: Int?, val token: String?) : LoginEvent()
    data class Error(val message: String) : LoginEvent()
}
