package dev.belalkhan.jetauth.commons

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Marker interface to represent a ViewState.
 * Used to define the UI state of the ViewModel.
 */
interface ViewState

/**
 * Marker interface to represent a ViewEvent.
 * Used to define the user actions or events in the ViewModel.
 */
interface ViewEvent

/**
 * An abstract ViewModel class to facilitate the Event-State architecture.
 *
 * This class helps in managing the state of the UI and responding to events in a structured way.
 * It follows the principles of Unidirectional Data Flow (UDF) and encourages separation of concerns.
 *
 * @param S The type of state, extending [ViewState], representing the UI state.
 * @param E The type of event, extending [ViewEvent], representing user actions or events.
 */
abstract class EventStateViewModel<S : ViewState, E : ViewEvent>() : ViewModel() {

    /**
     * Backing field for the current state of the UI. Should be overridden in the subclass.
     */
    protected abstract val _state: MutableStateFlow<S>

    /**
     * Publicly exposed immutable state flow. Observed by the UI to reflect changes.
     */
    val state: StateFlow<S> get() = _state

    /**
     * Processes incoming events to update the state or perform actions.
     * Should be implemented by the subclass to handle all possible events of type [E].
     *
     * @param event The event to be handled, of type [E].
     */
    abstract fun onEvent(event: E)

    /**
     * Safely updates the state of the ViewModel.
     *
     * @param newState The new state to be set.
     */
    protected fun updateState(newState: S) {
        _state.value = newState
    }
}

