package ny.photomap.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import ny.photomap.ui.mainmap.MainPhotoMapScreen
import ny.photomap.ui.navigation.Destination
import ny.photomap.ui.navigation.NavigationAction
import ny.photomap.ui.navigation.Navigator
import ny.photomap.ui.navigation.ObserveAsEvents
import ny.photomap.ui.photo.PhotoScreen

@Composable
fun PhotoMapNavHost(navigator: Navigator) {
    val navController = rememberNavController()

    ObserveAsEvents(flow = navigator.navigationActions) { action ->
        when (action) {
            is NavigationAction.Navigate -> navController.navigate(
                action.destination
            ) {
                action.navOptions(this)
            }

            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    NavHost(navController = navController, startDestination = navigator.startDestination) {
        navigation<Destination.MainGraph>(
            startDestination = Destination.PhotoMap,
        ) {
            composable<Destination.PhotoMap> {
                MainPhotoMapScreen(
                    modifier = Modifier,
                )
            }
            composable<Destination.Photo> {
                PhotoScreen()
            }
        }

        navigation<Destination.SettingGraph>(
            startDestination = Destination.Setting
        ) {
            composable<Destination.Setting> {

            }
        }

    }
}