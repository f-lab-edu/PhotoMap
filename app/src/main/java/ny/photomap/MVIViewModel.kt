package ny.photomap

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MVIViewModel<Intent, State, out Effect> {

    val effect: SharedFlow<Effect>

    val intent: Channel<Intent>

    val state: StateFlow<State>

    fun handleIntent(event: Intent)

    suspend fun reducer(state: State, intent: Intent): State

}