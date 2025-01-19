package ny.photomap.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ny.photomap.ui.mainmap.MainPhotoMapScreen
import ny.photomap.ui.photo.PhotoScreen

@Composable
fun PhotoMapNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Destination.PhotoMap) {
        composable<Destination.PhotoMap> {
            MainPhotoMapScreen(
                modifier = Modifier,
                onPhotoClick = { photoId ->
                    navController.navigate(Destination.Photo(photoId))
                }
            )
        }
        composable<Destination.Photo> {
            PhotoScreen(
                modifier = Modifier,
            )
        }
    }
}