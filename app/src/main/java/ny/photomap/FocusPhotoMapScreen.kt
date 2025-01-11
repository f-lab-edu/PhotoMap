package ny.photomap

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.GoogleMap


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FocusPhotoMapScreen(modifier: Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->





        Column(modifier = Modifier.fillMaxSize()) {

            GoogleMap(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize(),
            )
        }

    }

}