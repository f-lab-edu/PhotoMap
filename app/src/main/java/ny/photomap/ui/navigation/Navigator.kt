package ny.photomap.ui.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * reference :
 * 1) https://youtu.be/BFhVvAzC52w?si=wG4jD4Nvs1gCaY6T
 * 2) https://github.com/philipplackner/NavigationFromViewModel
 */
interface Navigator {
    val startDestination: Destination
    val navigationActions: Flow<NavigationAction>

    suspend fun navigate(
        destination: Destination,
        navOptions: NavOptionsBuilder.() -> Unit = {},
    )

    suspend fun navigateUp()
}

class PhotoMapNavigator(
    override val startDestination: Destination
): Navigator {
    private val _navigationActions = Channel<NavigationAction>(capacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,)
    override val navigationActions = _navigationActions.receiveAsFlow()

    override suspend fun navigate(
        destination: Destination,
        navOptions: NavOptionsBuilder.() -> Unit
    ) {
        _navigationActions.send(NavigationAction.Navigate(
            destination = destination,
            navOptions = navOptions
        ))
    }

    override suspend fun navigateUp() {
        _navigationActions.send(NavigationAction.NavigateUp)
    }
}