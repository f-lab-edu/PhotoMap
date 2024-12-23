package ny.photomap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import ny.photomap.model.LocationBoundsUIModel
import ny.photomap.permission.locationPermissions
import ny.photomap.permission.readImagePermissions
import ny.photomap.ui.marker.PhotoLocationClustering
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
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
        val permissionList =
            locationPermissions + readImagePermissions + listOf(Manifest.permission.ACCESS_MEDIA_LOCATION)

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
        val photoList by viewModel.photoList.collectAsStateWithLifecycle()

        val cameraPositionState = rememberCameraPositionState()

        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }

        var showAskingPermissionDialog by remember { mutableStateOf(false) }
        var isFirstAppUsage by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            viewModel.handleIntent(MainMapIntent.CheckSyncTime)
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                Timber.d("effect: $effect")
                when (effect) {
                    is MainMapEffect.RequestPermissions -> {
                        isFirstAppUsage = effect.isFirstAppUsage
                        showAskingPermissionDialog = true
                    }

                    MainMapEffect.NavigateToAppSetting -> context.startActivity(
                        Intent(
                            ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                        )
                    )

                    is MainMapEffect.NavigateToDetailLocationMap -> {

                    }

                    is MainMapEffect.NavigateToPhoto -> {

                    }

                    is MainMapEffect.Notice -> {
                        snackBarHostState.showSnackbar(message = context.getString(effect.message))
                    }

                    is MainMapEffect.Error -> {
                        snackBarHostState.showSnackbar(message = context.getString(effect.message))
                    }


                    MainMapEffect.MoveToCurrentLocation,
                        -> try {
                        @SuppressLint("MissingPermission")
                        val location = fusedLocationClient.lastLocation.await()
                        Timber.d("location : $location")
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), 15.0f
                            ), durationMs = 1500
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Timber.d("위치 조회 에러 : $e")
                    }

                }
            }
        }

        if (showAskingPermissionDialog) {
            AskingPermissionDialog(isFirstUsage = isFirstAppUsage, onConfirm = {
                showAskingPermissionDialog = false
                permissionState.launchMultiplePermissionRequest()
            }) {
                showAskingPermissionDialog = false
            }
        }

        LaunchedEffect(cameraPositionState.isMoving) {
            if (cameraPositionState.isMoving) {
                // 카메라 움직임 시작
                Timber.d("카메라 이동 : ${cameraPositionState.cameraMoveStartedReason.name}, 시작 지점 : ${cameraPositionState.position.target}")

            } else {
                // 카메라 움직임 끝
                Timber.d("카메라 이동 완료. 중심 : ${cameraPositionState.position.target}")
                cameraPositionState.projection?.visibleRegion?.latLngBounds?.let { latLngBounds ->
                    val northeast = latLngBounds.northeast
                    val southwest = latLngBounds.southwest
                    Timber.d("지도 보이는 영역 위경도 범위 - northeast : $northeast, southwest : $southwest")
                    viewModel.handleIntent(
                        MainMapIntent.LookAroundCurrentLocation(
                            LocationBoundsUIModel(
                                eastLongitude = northeast.longitude,
                                westLongitude = southwest.longitude,
                                northLatitude = northeast.latitude,
                                southLatitude = southwest.latitude,
                            )
                        )
                    )
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
                    onMapClick = { lnglat ->
                        Timber.d("lnglat : $lnglat")
                    },
                    uiSettings = MapUiSettings(rotationGesturesEnabled = false)
                ) {
                    PhotoLocationClustering(photoList)
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

            }

        }

        if (state.loading) {
            Timber.d("로딩")
            SyncLoadingScreen()
        }

    }
}