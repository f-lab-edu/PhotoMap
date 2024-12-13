package ny.photomap

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.GoogleMap
import kotlinx.coroutines.flow.collectLatest
import ny.photomap.model.AcceptPermissionState
import ny.photomap.model.FileAcceptPermissionState
import ny.photomap.model.LocationPermissionState
import ny.photomap.permission.locationPermissions
import ny.photomap.permission.readImagePermissions
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainPhotoMapScreen(
    modifier: Modifier,
    viewModel: MainMapViewModel = viewModel(),
) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->

        val context = LocalContext.current
        val permissionList = locationPermissions + readImagePermissions

        val permissionState =
            rememberMultiplePermissionsState(
                permissions = permissionList,
                onPermissionsResult = { map ->
                    val imageAccessPermission =
                        map.getOrDefault(readImagePermissions.firstOrNull(), false)
                    val imageVisualUserSelectedPermission = map.getOrDefault(
                        if (readImagePermissions.size > 1) readImagePermissions.last() else null,
                        false
                    )
                    val locationPermission = locationPermissions.any { map.getOrDefault(it, false) }

                    val permissionResponse = AcceptPermissionState(
                        filePermission = FileAcceptPermissionState(
                            acceptedPermission = imageAccessPermission,
                            visualUserSelectedPermission = imageVisualUserSelectedPermission
                        ),
                        locationPermission = LocationPermissionState(
                            acceptedPermission = locationPermission
                        )
                    )
                    Timber.d("permission response : $permissionResponse")
                    viewModel.handleIntent(MainMapIntent.ResponsePermissionRequest(permissionState = permissionResponse))
                }
            )

        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.handleIntent(MainMapIntent.CheckSyncTime)
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    MainMapEffect.RequestPermissions -> permissionState.launchMultiplePermissionRequest()

                    MainMapEffect.NavigateToAppSetting -> context.startActivity(
                        Intent(
                            ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        )
                    )

                    is MainMapEffect.NavigateToDetailLocationMap -> {}
                    is MainMapEffect.NavigateToPhoto -> {}
                    is MainMapEffect.Error -> {}
                    MainMapEffect.MoveToCurrentLocation -> {}
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box {
                GoogleMap(
                    modifier = modifier
                        .fillMaxSize(),
                )
                Column(modifier = Modifier.align(alignment = Alignment.TopEnd)) {

                    SyncWithPermissionNoticeExtendedFloatButton(
                        modifier = Modifier.align(Alignment.End),
                        permissionState = permissionState,
                        targetPermissionList = readImagePermissions,
                        onClick = { hasPermission ->
                            if (hasPermission) viewModel.handleIntent(MainMapIntent.Sync)
                            else viewModel.handleIntent(MainMapIntent.GoToAcceptPermission)
                        }
                    )

                    CurrentLocationWithPermissionNoticeExtendedFloatButton(
                        modifier = Modifier.align(Alignment.End),
                        permissionState = permissionState,
                        targetPermissionList = locationPermissions,
                        onClick = { hasPermission ->
                            if (hasPermission) viewModel
                            else viewModel.handleIntent(MainMapIntent.GoToAcceptPermission)
                        }
                    )

                }

            }

        }

    }

}