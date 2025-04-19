package ny.photomap.ui.mainmap

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ny.photomap.BuildConfig
import ny.photomap.model.LocationBoundsUIModel
import ny.photomap.model.LocationUIModel
import ny.photomap.permission.locationPermissions
import ny.photomap.permission.readImagePermissions
import ny.photomap.ui.marker.PhotoLocationClustering
import ny.photomap.ui.photo.CircleShapeBackgroundText
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class)
@Composable
fun MainPhotoMapScreen(
    modifier: Modifier,
    viewModel: MainMapViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackBarHostState) {
                SuccessFailureSnackbar(it)
            }

        }) { padding ->

        val context = LocalContext.current
        val state by viewModel.state.collectAsStateWithLifecycle()

        val readImagePermissionState = rememberMultiplePermissionsState(readImagePermissions,
            onPermissionsResult = { map ->
                val imageAccessPermission =
                    map.getOrDefault(readImagePermissions.firstOrNull(), false)
                val imageVisualUserSelectedPermission = map.getOrDefault(
                    if (readImagePermissions.size > 1) readImagePermissions.last() else null,
                    false
                )
                Timber.d("imageAccessPermission: $imageAccessPermission, imageVisualUserSelectedPermission: $imageVisualUserSelectedPermission")
                if (imageAccessPermission || imageVisualUserSelectedPermission) {
                    viewModel.handleIntent(MainMapIntent.Sync)
                } else {
                    viewModel.handleIntent(MainMapIntent.ResetViewState)
                }
            })

        val locationPermissionState =
            rememberMultiplePermissionsState(locationPermissions, onPermissionsResult = { map ->
                val locationPermission =
                    locationPermissions.any { map.getOrDefault(it, false) }
                if (locationPermission) {
                    viewModel.handleIntent(MainMapIntent.SearchCurrentLocation)
                } else {
                    viewModel.handleIntent(MainMapIntent.DenyLocationPermission)
                }
            })

        val photoList by viewModel.photoList.collectAsStateWithLifecycle()

        val cameraPositionState = rememberCameraPositionState()

        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(context) }


        LaunchedEffect(Unit) {
            if (viewModel.isInitializationNeeded) {
                viewModel.handleIntent(MainMapIntent.CheckSyncTime)
            }

            withContext(Dispatchers.Default) {


                viewModel.effect.collectLatest { effect ->
                    Timber.d("effect: $effect")
                    when (effect) {
                        is MainMapEffect.RequestLocationPermissions -> {
                            locationPermissionState.launchMultiplePermissionRequest()
                        }

                        MainMapEffect.NavigateToAppSetting -> withContext(Dispatchers.Main) {
                            context.startActivity(
                                Intent(
                                    ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                                )
                            )
                        }

                        is MainMapEffect.NavigateToPhoto -> {
                            viewModel.onPhotoClick(effect.photoId)
                        }

                        is MainMapEffect.Notice -> withContext(Dispatchers.Main) {
                            snackBarHostState.showSnackbar(message = context.getString(effect.message))
                        }

                        is MainMapEffect.Error -> withContext(Dispatchers.Main) {
                            snackBarHostState.showSnackbar(message = context.getString(effect.message))
                        }


                        MainMapEffect.MoveToCurrentLocation,
                            -> try {
                            @SuppressLint("MissingPermission")
                            val location = fusedLocationClient.lastLocation.await()
                            Timber.d("location : $location")
                            withContext(Dispatchers.Main) {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            location.latitude,
                                            location.longitude
                                        ), 15.0f
                                    ), durationMs = 1500
                                )
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Timber.d("위치 조회 에러 : $e")
                        }

                    }
                }
            }

        }

        if (state.showPermissionDialog) {
            AskingPermissionDialog(isFirstUsage = state.isFirstAppUsage, onConfirm = {
                viewModel.handleIntent(MainMapIntent.ResetViewState)
                readImagePermissionState.launchMultiplePermissionRequest()
            }) {
                viewModel.handleIntent(MainMapIntent.ResetViewState)
            }
        }

        LaunchedEffect(cameraPositionState.isMoving) {
            if (!cameraPositionState.isMoving) {
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
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = false),
                    onMapClick = { lnglat ->
                        Timber.d("lnglat : $lnglat")
                    },
                    uiSettings = MapUiSettings(
                        rotationGesturesEnabled = false,
                        indoorLevelPickerEnabled = false,
                        mapToolbarEnabled = false,
                        zoomControlsEnabled = false
                    )
                ) {
                    PhotoLocationClustering(
                        items = photoList,
                        onPhotoItemClick = { photoId ->
                            viewModel.handleIntent(
                                MainMapIntent.SelectPhoto(
                                    photoId = photoId
                                )
                            )
                        },
                        onPhotoClusteringClick = { clusteringLocation, clusteringList ->
                            viewModel.handleIntent(
                                MainMapIntent.SelectClusteringLocationMarker(
                                    photoList = clusteringList,
                                    clusteringLocation = LocationUIModel(
                                        latitude = clusteringLocation.latitude,
                                        longitude = clusteringLocation.longitude,
                                        location = null
                                    )
                                )
                            )
                        }
                    )
                }

                Column(modifier = Modifier.align(alignment = Alignment.TopEnd)) {

                    CurrentLocationWithPermissionNoticeExtendedFloatButton(
                        modifier = Modifier.align(Alignment.End),
                        permissionState = locationPermissionState,
                        targetPermissionList = locationPermissions,
                        onClick = { hasPermission ->
                            if (hasPermission) viewModel.handleIntent(MainMapIntent.SearchCurrentLocation)
                            else viewModel.handleIntent(MainMapIntent.GoToAcceptPermission)
                        }
                    )

                    SyncWithPermissionNoticeExtendedFloatButton(
                        modifier = Modifier.align(Alignment.End),
                        permissionState = readImagePermissionState,
                        targetPermissionList = readImagePermissions,
                        onClick = { hasPermission ->
                            if (hasPermission) viewModel.handleIntent(MainMapIntent.Sync)
                            else viewModel.handleIntent(MainMapIntent.GoToAcceptPermission)
                        }
                    )
                }

                if (state.targetLocationPhotoList.isNotEmpty()) {
                    Timber.d("photo list size : ${state.targetLocationPhotoList.size}")
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        if (state.cameraLocation.location != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        bottom = dimensionResource(ny.photomap.R.dimen.margin_medium),
                                        start = dimensionResource(ny.photomap.R.dimen.margin_small),
                                        end = dimensionResource(ny.photomap.R.dimen.margin_small),
                                    )
                            ) {
                                Spacer(modifier = Modifier.weight(0.01f))
                                CircleShapeBackgroundText(
//                                    modifier = Modifier.weight(1f),
                                    backgroundColor = Color.White,
                                    text = state.cameraLocation.location ?: "",
                                    textColor = Color.Black
                                )
                                Spacer(modifier = Modifier.weight(0.01f))
                            }

                        }

                        val lazyListStat = rememberLazyListState()
                        LazyRow(
                            modifier = Modifier
                                .height(dimensionResource(ny.photomap.R.dimen.size_thumbnail)),
                            state = lazyListStat
                        ) {
                            items(state.targetLocationPhotoList.size) { index ->
                                val photo = state.targetLocationPhotoList[index]
                                AsyncImage(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(BorderStroke(1.dp, Color.White))
                                        .clickable {
                                            viewModel.handleIntent(
                                                MainMapIntent.SelectPhoto(
                                                    photoId = photo.id
                                                )
                                            )
                                        },

                                    model = ImageRequest.Builder(context)
                                        .data(photo.uri) // todo thumbnail로 교체
                                        .size(
                                            context.resources.getDimensionPixelSize(ny.photomap.R.dimen.size_thumbnail),
                                            context.resources.getDimensionPixelSize(ny.photomap.R.dimen.size_thumbnail)
                                        )
                                        .crossfade(true)
                                        .allowHardware(false)
                                        .build(),
                                    contentScale = ContentScale.Crop,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.loading) {
            Timber.d("로딩")
            SyncLoadingScreen()
        }

    }
}