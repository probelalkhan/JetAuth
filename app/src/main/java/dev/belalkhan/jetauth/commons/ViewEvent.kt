package dev.belalkhan.jetauth.commons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@Composable
fun <E : Any> ViewEvent(
    eventDispatcher: Flow<E>,
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    collector: suspend (E) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(eventDispatcher) {
        lifecycleOwner.repeatOnLifecycle(lifecycleState) {
            withContext(Dispatchers.Main.immediate) {
                eventDispatcher.onEach(collector).collect()
            }
        }
    }
}
