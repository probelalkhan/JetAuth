package dev.belalkhan.jetauth.home.ui

import dev.belalkhan.jetauth.commons.ViewEvent
import dev.belalkhan.jetauth.commons.ViewState

sealed class HomeEvent : ViewEvent {
    object ShowLogoutDialog : HomeEvent()
    object ConfirmLogout : HomeEvent()
    object DismissLogoutDialog : HomeEvent()
}

data class HomeState(
    val showLogoutDialog: Boolean = false,
) : ViewState

sealed class HomeNavigationEvents {
    data object LoggedOut : HomeNavigationEvents()
}