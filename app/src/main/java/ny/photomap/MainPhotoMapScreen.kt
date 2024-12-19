package ny.photomap

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import ny.photomap.model.LocationUIModel
import ny.photomap.permission.locationPermissions
import ny.photomap.permission.readImagePermissions
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainPhotoMapScreen(
    modifier: Modifier,
    viewModel: MainMapViewModel = viewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackBarHostState) {
                SuccessFailureSnackbar(it)
            }

        }) { padding ->

        val context = LocalContext.current
        val permissionList = locationPermissions + readImagePermissions

        val permissionState =
            rememberMultiplePermissionsState(
                permissions = permissionList,
                onPermissionsResult = { map ->
                    Timber.d("onPermissionsResult")
                    val imageAccessPermission =
                        map.getOrDefault(readImagePermissions.firstOrNull(), false)
                    val imageVisualUserSelectedPermission = map.getOrDefault(
                        if (readImagePermissions.size > 1) readImagePermissions.last() else null,
                        false
                    )
                    val locationPermission = locationPermissions.any { map.getOrDefault(it, false) }

                    if (imageAccessPermission || imageVisualUserSelectedPermission) {
                        viewModel.handleIntent(MainMapIntent.Sync)
                    } else {
                        viewModel.handleIntent(MainMapIntent.DenyFilePermission)
                    }

                    if (locationPermission) {
                        viewModel.handleIntent(MainMapIntent.SearchCurrentLocation)
                    } else {
                        viewModel.handleIntent(MainMapIntent.DenyLocationPermission)
                    }

                }
            )

        val state by viewModel.state.collectAsStateWithLifecycle()

        val cameraPositionState = rememberCameraPositionState()


        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }

        LaunchedEffect(Unit) {
            viewModel.handleIntent(MainMapIntent.CheckSyncTime)
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                Timber.d("effect: $effect")
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
                    is MainMapEffect.Notice -> {
                        snackBarHostState.showSnackbar(message = context.getString(effect.message))
                    }

                    is MainMapEffect.Error -> {
                        snackBarHostState.showSnackbar(message = context.getString(effect.message))
                    }

                    MainMapEffect.MoveToCurrentLocation -> {
                        try {
                            val location = fusedLocationClient.lastLocation.await()
                            Timber.d("location : $location")
                            cameraPositionState.animate(
                                update =
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    ), 15.0f
                                ), durationMs = 1500
                            )
                            viewModel.handleIntent(
                                MainMapIntent.LookAroundCurrentLocation(
                                    LocationUIModel(
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Timber.d("위치 조회 에러 : e")
                        }

                    }
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
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                ) {

                }

                Column(modifier = Modifier.align(alignment = Alignment.TopEnd)) {

                    CurrentLocationWithPermissionNoticeExtendedFloatButton(
                        modifier = Modifier.align(Alignment.End),
                        permissionState = permissionState,
                        targetPermissionList = locationPermissions,
                        onClick = { hasPermission ->
                            if (hasPermission) viewModel.handleIntent(MainMapIntent.SearchCurrentLocation)
                            else viewModel.handleIntent(MainMapIntent.GoToAcceptPermission)
                        }
                    )

                    SyncWithPermissionNoticeExtendedFloatButton(
                        modifier = Modifier.align(Alignment.End),
                        permissionState = permissionState,
                        targetPermissionList = readImagePermissions,
                        onClick = { hasPermission ->
                            if (hasPermission) viewModel.handleIntent(MainMapIntent.Sync)
                            else viewModel.handleIntent(MainMapIntent.GoToAcceptPermission)
                        }
                    )


                }

                if (state.loading) {
                    Timber.d("로딩")
                    SyncLoadingScreen()
                }

            }

        }

    }

}