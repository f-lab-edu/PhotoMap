package ny.photomap

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.maps.android.compose.GoogleMap
import kotlinx.coroutines.flow.collectLatest
import ny.photomap.model.AcceptPermissionState
import ny.photomap.model.FileAcceptPermissionState
import ny.photomap.model.LocationPermissionState
import ny.photomap.permission.PermissionRequestNotice
import ny.photomap.permission.isGranted
import ny.photomap.permission.isGrantedReadMediaVisualUserSelected
import ny.photomap.permission.locationPermissions
import ny.photomap.permission.readImagePermission
import ny.photomap.permission.readImagePermissions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainPhotoMapScreen(
    modifier: Modifier,
    viewModel: MainMapViewModel = viewModel(),
) {
    Scaffold(modifier = modifier.fillMaxSize()) { padding ->

        val context = LocalContext.current
        val fileReadPermission = readImagePermission

        val permissionState =
            rememberMultiplePermissionsState(
                permissions = fileReadPermission.permissionList ?: emptyList()
            )

        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.handleIntent(MainMapIntent.CheckSyncTime)
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    MainMapEffect.RequestImagePermission -> permissionState.launchMultiplePermissionRequest()

                    MainMapEffect.NavigateToAppSetting -> context.startActivity(
                        Intent(
                            ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        )
                    )

                    is MainMapEffect.NavigateToDetailLocationMap -> {}
                    is MainMapEffect.NavigateToPhoto -> {}
                    is MainMapEffect.Error -> {}
                }
            }
        }

        LaunchedEffect(permissionState) {
            val permissionResponse = AcceptPermissionState(
                filePermission = FileAcceptPermissionState(
                    acceptedPermission = permissionState.isGranted(readImagePermissions.firstOrNull()),
                    visualUserSelectedPermission = permissionState.isGrantedReadMediaVisualUserSelected()
                ),
                locationPermission = LocationPermissionState(
                    acceptedPermission = locationPermissions.any { permissionState.isGranted(it) }
                )
            )
            println("파일 권한 : ${permissionState.isGranted(readImagePermissions.firstOrNull())}")
            println("파일 권한 사용자  : ${permissionState.isGrantedReadMediaVisualUserSelected()}")
            println("위치 권한 : ${locationPermissions.any { permissionState.isGranted(it) }}")
            viewModel.handleIntent(MainMapIntent.ResponsePermissionRequest(permissionState = permissionResponse))
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            println("** 파일 권한 : ${state.permissionState.filePermission.acceptedPermission}")
            if (!state.permissionState.filePermission.acceptedPermission) {
                val title: String = stringResource(R.string.permission_read_image_title)
                val description: String =
                    if (state.permissionState.filePermission.visualUserSelectedPermission) {
                        stringResource(R.string.permission_read_image_visual_user_selected_description)
                    } else {
                        stringResource(R.string.permission_read_image_description)
                    }

                PermissionRequestNotice(
                    modifier = Modifier,
                    title = title,
                    description = description,
                    requestButtonText = "권한 요청하기",
                    onClickRequestButton = {

                    }
                )
            }
            Box {
                GoogleMap(
                    modifier = modifier
                        .fillMaxSize(),
                )
                Column(modifier = Modifier.align(alignment = Alignment.TopEnd)) {

                    SmallFloatingActionButton(
                        onClick = {
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Filled.Refresh, "Small floating action button.")
                    }


                    SmallFloatingActionButton(
                        onClick = {

                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Icon(Icons.Filled.LocationOn, "Small floating action button.")
                    }
                }

            }

        }

    }

}