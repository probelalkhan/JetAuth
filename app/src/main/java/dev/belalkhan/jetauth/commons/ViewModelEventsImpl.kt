package dev.belalkhan.jetauth.commons

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ViewModelEventsImpl<E : Any> @Inject constructor() : ViewModelEvents<E> {
    private val eventsChannel = Channel<E>(Channel.BUFFERED)
    override val eventsFlow: Flow<E>
        get() = eventsChannel.receiveAsFlow()

    override suspend fun sendEvent(event: E) {
        withContext(Dispatchers.Main.immediate) {
            eventsChannel.send(event)
        }
    }

    override fun end() {
        eventsChannel.close()
    }
}
