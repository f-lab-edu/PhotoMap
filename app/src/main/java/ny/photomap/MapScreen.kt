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
import ny.photomap.permission.ReadImagePermission

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(modifier: Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->

        val permission = ReadImagePermission
        val permissionState = rememberMultiplePermissionsState(permission.permission)
        var visualUserSelectedGranted by remember { mutableStateOf(false) }
        var onDenied by remember { mutableStateOf(false) }
        PermissionRequester(
            permission = permission,
            permissionsState = permissionState,
            onAllGranted = {
                // todo 이미지 파일 조회
            },
            onVisualUserSelectedGranted = {
                visualUserSelectedGranted = true
            },
            onDenied = { _ ->
                onDenied = true
            })

        Column(modifier = Modifier.fillMaxSize()) {
            if (visualUserSelectedGranted || onDenied) {
                val title: String = stringResource(permission.titleRes)
                val description: String = if (visualUserSelectedGranted) {
                    stringResource(permission.visualUserSelectedDescriptionRes)
                } else {
                    stringResource(permission.needDescriptionRes)
                }

                //todo UI 작업
            }
            GoogleMap(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize(),
            )
        }

    }

}