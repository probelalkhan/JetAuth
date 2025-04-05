package dev.belalkhan.jetauth.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.belalkhan.jetauth.auth.ui.LoginNavigationEvents
import dev.belalkhan.jetauth.commons.ViewModelEvents
import dev.belalkhan.jetauth.commons.ViewModelEventsImpl

@InstallIn(ViewModelComponent::class)
@Module
abstract class AuthEventsModule {
    @Binds
    abstract fun bindLoginViewModelEvents(
        impl: ViewModelEventsImpl<LoginNavigationEvents>,
    ): ViewModelEvents<LoginNavigationEvents>
}