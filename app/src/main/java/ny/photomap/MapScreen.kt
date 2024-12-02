package ny.photomap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap

@Composable
fun MapScreen(modifier: Modifier) {
    GoogleMap(
        modifier = modifier.fillMaxSize(),
    )
}