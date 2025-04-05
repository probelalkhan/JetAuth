package dev.belalkhan.jetauth.auth.ui

import dev.belalkhan.jetauth.commons.ViewEvent
import dev.belalkhan.jetauth.commons.ViewState

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
) : ViewState


sealed class LoginEvent : ViewEvent {
    data class UserNameChanged(val username: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object TogglePasswordVisibility : LoginEvent()
    data object LoginClicked : LoginEvent()
}

sealed class LoginNavigationEvents {
    data object Authenticated : LoginNavigationEvents()
}