package dev.belalkhan.jetauth.home

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.belalkhan.jetauth.commons.ViewModelEvents
import dev.belalkhan.jetauth.commons.ViewModelEventsImpl
import dev.belalkhan.jetauth.home.ui.HomeNavigationEvents

@InstallIn(ViewModelComponent::class)
@Module
abstract class HomeEventsModule {
    @Binds
    abstract fun bindHomeViewModelEvents(
        impl: ViewModelEventsImpl<HomeNavigationEvents>,
    ): ViewModelEvents<HomeNavigationEvents>
}