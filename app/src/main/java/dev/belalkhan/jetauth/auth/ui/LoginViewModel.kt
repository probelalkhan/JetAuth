package dev.belalkhan.jetauth.auth.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.belalkhan.jetauth.auth.data.LoginResponse
import dev.belalkhan.jetauth.auth.data.LoginUseCase
import dev.belalkhan.jetauth.commons.EventStateViewModel
import dev.belalkhan.jetauth.commons.ViewModelEvents
import dev.belalkhan.jetauth.data.network.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    viewModelEvents: ViewModelEvents<LoginNavigationEvents>,
) : EventStateViewModel<LoginState, LoginEvent>(),
    ViewModelEvents<LoginNavigationEvents> by viewModelEvents {

    override val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UserNameChanged -> {
                updateState(_state.value.copy(username = event.username, usernameError = null))
            }

            is LoginEvent.PasswordChanged -> {
                updateState(_state.value.copy(password = event.password, passwordError = null))
            }

            is LoginEvent.TogglePasswordVisibility -> {
                updateState(_state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible))
            }

            is LoginEvent.LoginClicked -> {
                if (_state.value.username.isBlank()) {
                    updateState(_state.value.copy(usernameError = "Username cannot be empty"))
                    return
                }
                if (_state.value.password.length < 6) {
                    updateState(_state.value.copy(passwordError = "Password must be at least 6 characters"))
                    return
                }
                login()
            }
        }
    }

    private fun login() = viewModelScope.launch {
        updateState(_state.value.copy(isLoading = true))

        val username = _state.value.username
        val password = _state.value.password
        val loginResult = loginUseCase(username, password)

        updateState(_state.value.copy(isLoading = false))

        when (loginResult) {
            is NetworkResult.Success<LoginResponse> -> {
                sendEvent(LoginNavigationEvents.Authenticated)
            }

            is NetworkResult.Error.ClientError -> {
                updateState(_state.value.copy(loginError = loginResult.message))
            }

            else -> {}
        }
    }
}