package ny.photomap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ny.photomap.ui.PhotoMapNavHost
import ny.photomap.ui.navigation.Navigator
import ny.photomap.ui.theme.PhotoMapTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PhotoMapTheme {
                PhotoMapNavHost(navigator)
            }
        }
    }


}