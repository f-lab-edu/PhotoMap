package ny.photomap.ui.mainmap

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ny.photomap.BaseViewModel
import ny.photomap.MVIEffect
import ny.photomap.MVIIntent
import ny.photomap.MVIState
import ny.photomap.R
import ny.photomap.domain.model.PhotoLocationEntityModel
import ny.photomap.domain.onResponse
import ny.photomap.domain.usecase.CheckSyncStateUseCase
import ny.photomap.domain.usecase.GetLocationTextUseCase
import ny.photomap.domain.usecase.GetPhotoLocationsInBoundaryUseCase
import ny.photomap.domain.usecase.SyncPhotoUseCase
import ny.photomap.model.LocationBoundsUIModel
import ny.photomap.model.LocationUIModel
import ny.photomap.model.PhotoLocationUIModel
import ny.photomap.model.toPhotoLocationUiModel
import ny.photomap.ui.navigation.Destination
import ny.photomap.ui.navigation.Navigator
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.List

sealed interface MainMapIntent : MVIIntent {

    // 싱크 타이밍 확인
    object CheckSyncTime : MainMapIntent

    // 싱크 진행
    object Sync : MainMapIntent

    // 싱크 종료
    object SyncFinished : MainMapIntent

    // 화면 뷰 상태 초기화. 파일 권한 거부시 사용
    object ResetViewState : MainMapIntent

    // 위치 권한 거부
    object DenyLocationPermission : MainMapIntent

    // 현재 위치로 이동
    object SearchCurrentLocation : MainMapIntent

    // 현재 위치의 지도 범위의 지도의 사진들 보기
    data class LookAroundCurrentLocation(val locationBounds: LocationBoundsUIModel) : MainMapIntent

    // 사진 마커 선택
    data class SelectClusteringLocationMarker(
        val photoList: Array<PhotoLocationUIModel>,
        val clusteringLocation: LocationUIModel,
    ) : MainMapIntent

    // 사진 선택
    data class SelectPhoto(val photoId: Long) : MainMapIntent

    // 권한 주겠다
    object GoToAcceptPermission : MainMapIntent
}

data class MainMapState(
    val cameraLocation: LocationUIModel = LocationUIModel(100.0, 100.0, null),
    val targetLocationPhotoList: Array<PhotoLocationUIModel> = emptyArray(),
    val loading: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val isFirstAppUsage: Boolean = false,
) : MVIState

sealed interface MainMapEffect : MVIEffect {
    object RequestLocationPermissions : MainMapEffect

    object NavigateToAppSetting : MainMapEffect
    object MoveToCurrentLocation : MainMapEffect
    data class NavigateToPhoto(val photoId: Long) : MainMapEffect
    data class Notice(@StringRes val message: Int) : MainMapEffect
    data class Error(@StringRes val message: Int) : MainMapEffect
}

@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val checkSyncState: CheckSyncStateUseCase,
    private val syncPhoto: SyncPhotoUseCase,
    private val getPhotoLocationsInBoundary: GetPhotoLocationsInBoundaryUseCase,
    private val getLocationText: GetLocationTextUseCase,
    private val navigator: Navigator,
) : BaseViewModel<MainMapIntent, MainMapState, MainMapEffect>() {

    private val _effect = MutableSharedFlow<MainMapEffect>()
    override val effect: SharedFlow<MainMapEffect> = _effect

    override val intent = Channel<MainMapIntent>()

    override val state = intent.receiveAsFlow().runningFold(MainMapState()) { state, intent ->
        withContext(Dispatchers.Default) {
            reducer(state, intent)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainMapState(loading = false))

    private val _photoList = MutableStateFlow<List<PhotoLocationUIModel>>(emptyList())
    val photoList: StateFlow<List<PhotoLocationUIModel>> = _photoList

    // 캐시용 todo 고도화 필요
    private var previousBounds: LocationBoundsUIModel? = null
    private val photoCache = mutableMapOf<LocationBoundsUIModel, List<PhotoLocationUIModel>>()

    // 화면 첫 진입 시 현재 위치로 이동 필요 상태
    var isInitializationNeeded: Boolean = true
        private set

    override fun handleIntent(event: MainMapIntent) {
        viewModelScope.launch(Dispatchers.Default) {
            intent.send(event)
        }
    }

    override suspend fun reducer(state: MainMapState, intent: MainMapIntent): MainMapState {
        Timber.d("state: $state\nintent: $intent")
        return when (intent) {
            MainMapIntent.CheckSyncTime -> checkSyncStateAndRequestPermission(state).also {
                isInitializationNeeded = false
            }

            MainMapIntent.Sync -> {
                // todo 작업 중 노티피케이션
                sync()
                state.copy(loading = true)
            }

            MainMapIntent.SyncFinished -> state.copy(loading = false)

            is MainMapIntent.SelectClusteringLocationMarker -> state.copy(
                cameraLocation = if (intent.clusteringLocation.location.isNullOrEmpty()) {
                    val location = getLocationText.invoke(
                        latitude = intent.clusteringLocation.latitude,
                        longitude = intent.clusteringLocation.longitude
                    )
                    intent.clusteringLocation.copy(location = location)
                } else intent.clusteringLocation,
                targetLocationPhotoList = intent.photoList,
                loading = false,
            )

            is MainMapIntent.SelectPhoto -> state.apply {
                _effect.emit(MainMapEffect.NavigateToPhoto(photoId = intent.photoId))
            }

            MainMapIntent.ResetViewState -> MainMapState()

            MainMapIntent.DenyLocationPermission -> state.apply {
                _effect.emit(MainMapEffect.Notice(R.string.notice_deny_location_permission))
            }

            MainMapIntent.GoToAcceptPermission -> state.apply {
                _effect.emit(MainMapEffect.NavigateToAppSetting)
            }

            MainMapIntent.SearchCurrentLocation -> state.apply {
                _effect.emit(MainMapEffect.MoveToCurrentLocation)
            }

            is MainMapIntent.LookAroundCurrentLocation -> state.apply {
                getPhotoListByLocationBounds(intent.locationBounds)
            }
        }
    }

    private suspend fun checkSyncStateAndRequestPermission(state: MainMapState): MainMapState {
        return checkSyncState()
            .onResponse(ifSuccess = {
                Timber.d("싱크 진행을 위해 권한 요청")
                Timber.d("싱크 진행 여부 : ${it.shouldSync}, 최근 업데이트 시간 : ${it.lastSyncTime}")
                if (it.shouldSync) {
                    state.copy(
                        showPermissionDialog = true,
                        isFirstAppUsage = it.lastSyncTime == 0L,
                    )
                } else {
                    Timber.d("싱크 없이 진행")
                    _effect.emit(
                        MainMapEffect.RequestLocationPermissions
                    )
                    state.copy(
                        loading = false
                    )
                }
            }, ifFailure = {
                Timber.d("싱크 없이 진행")
                _effect.emit(
                    MainMapEffect.RequestLocationPermissions
                )
                state
            })
    }

    private fun sync() {
        viewModelScope.launch(Dispatchers.Default) {
            syncPhoto().onResponse(ifSuccess = {
                Timber.d("싱크 완료")
                _effect.emit(MainMapEffect.Notice(message = R.string.sync_update_complete))
                handleIntent(MainMapIntent.SyncFinished)
            }, ifFailure = {
                it?.printStackTrace()
                Timber.d("싱크 실패")
                _effect.emit(MainMapEffect.Error(message = R.string.sync_update_fail))
                handleIntent(MainMapIntent.SyncFinished)
            })
        }
    }

    private suspend fun getPhotoListByLocationBounds(locationBounds: LocationBoundsUIModel) {
        val shouldNotLoad = (previousBounds != null &&
                (previousBounds!!.northLatitude >= locationBounds.northLatitude &&
                        previousBounds!!.southLatitude <= locationBounds.southLatitude &&
                        previousBounds!!.westLongitude <= locationBounds.westLongitude &&
                        previousBounds!!.eastLongitude >= locationBounds.eastLongitude)
                )

        if (shouldNotLoad) return

        val cacheBounds: LocationBoundsUIModel? = photoCache.keys.find { bounds ->
            bounds.northLatitude >= locationBounds.northLatitude &&
                    bounds.southLatitude <= locationBounds.southLatitude &&
                    bounds.westLongitude <= locationBounds.westLongitude &&
                    bounds.eastLongitude >= locationBounds.eastLongitude
        }

        if (cacheBounds != null) {
            _photoList.value = photoCache[cacheBounds]!!
            previousBounds = locationBounds
        } else {
            val list = getPhotoLocationsInBoundary(
                northLatitude = locationBounds.northLatitude,
                southLatitude = locationBounds.southLatitude,
                eastLongitude = locationBounds.eastLongitude,
                westLongitude = locationBounds.westLongitude
            ).onResponse(ifSuccess = {
                it.map(PhotoLocationEntityModel::toPhotoLocationUiModel)
            }, ifFailure = {
                it?.printStackTrace()
                Timber.d("싱크 실패")
                _effect.emit(MainMapEffect.Error(message = R.string.load_photo_list_fail))
                emptyList<PhotoLocationUIModel>()
            })

            Timber.d("사진 조회 범위- $locationBounds")
            Timber.d("result size : ${list.size}")

            _photoList.value = list
            photoCache[locationBounds] = list
            previousBounds = locationBounds
        }
    }

    fun onPhotoClick(photoId: Long) {
        viewModelScope.launch {
            navigator.navigate(Destination.Photo(photoId))
        }
    }

}