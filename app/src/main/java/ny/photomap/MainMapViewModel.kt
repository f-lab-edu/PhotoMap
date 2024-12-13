package ny.photomap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ny.photomap.domain.onResponse
import ny.photomap.domain.usecase.CheckSyncStateUseCase
import ny.photomap.domain.usecase.SyncPhotoUseCase
import ny.photomap.model.AcceptPermissionState
import ny.photomap.model.LocationUIModel
import ny.photomap.model.PhotoLocationUIModel
import javax.inject.Inject
import kotlin.collections.List

sealed interface MainMapIntent {

    // 싱크 타이밍 확인
    object CheckSyncTime : MainMapIntent

    // 싱크 진행
    object Sync : MainMapIntent

    // 현재 위치로 이동
    object SearchAndMoveToCurrentLocation: MainMapIntent

    // 현재 위치로 이동해 지도의 사진들 보기
    data class LookAroundCurrentLocation(val location: LocationUIModel): MainMapIntent

    // 사진 마커 선택
    data class SelectPhotoLocationMarker(
        val photoList: List<String>,
        val targetPhoto: PhotoLocationUIModel,
    ) : MainMapIntent

    // 사진 위치 정보 선택f
    data class SelectLocation(val targetPhoto: PhotoLocationUIModel) : MainMapIntent

    // 사진 선택
    data class SelectPhoto(val targetPhoto: PhotoLocationUIModel) : MainMapIntent

    // 권한 요청에 응답함
    data class ResponsePermissionRequest(val permissionState: AcceptPermissionState) : MainMapIntent

    // 권한 주겠다
    object GoToAcceptPermission : MainMapIntent
}

data class MainMapState(
    val cameraLocation: LocationUIModel = LocationUIModel(100.0, 100.0),
    val photoList: List<String> = emptyList(),
    val targetPhoto: PhotoLocationUIModel? = null,
    val loading: Boolean = false,
    val permissionState: AcceptPermissionState = AcceptPermissionState(),
)

sealed interface MainMapEffect {
    object RequestPermissions : MainMapEffect
    object NavigateToAppSetting : MainMapEffect
    object MoveToCurrentLocation : MainMapEffect
    data class NavigateToDetailLocationMap(val targetPhoto: PhotoLocationUIModel) : MainMapEffect
    data class NavigateToPhoto(val targetPhoto: PhotoLocationUIModel) : MainMapEffect
    data class Error(val message: String) : MainMapEffect
}

@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val checkSyncState: CheckSyncStateUseCase,
    private val syncPhoto: SyncPhotoUseCase,
) : ViewModel() {

    private val _effect = MutableSharedFlow<MainMapEffect>()
    val effect: SharedFlow<MainMapEffect> = _effect

    private val intent = Channel<MainMapIntent>()

    val state = intent.receiveAsFlow().runningFold(MainMapState(), ::reducer)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainMapState(loading = true))

    fun handleIntent(event: MainMapIntent) {
        viewModelScope.launch {
            intent.send(event)
        }
    }

    private suspend fun reducer(state: MainMapState, intent: MainMapIntent): MainMapState {
        return when (intent) {
            MainMapIntent.CheckSyncTime -> checkSyncState()
                .onResponse(ifSuccess = {
                    _effect.emit(MainMapEffect.RequestPermissions)
                    state.copy(loading = true)
                }, ifFailure = {
                    // 룸 데이터 긇어와서 화면에 노출
                    // getphoto
                    state.copy(loading = false)
                })

            MainMapIntent.Sync -> syncPhoto().onResponse(ifSuccess = {
                //getphoto하고 노출
                // 화면에 노출
                state
            }, ifFailure = {
                // 로
                _effect.emit(MainMapEffect.Error(message = ""))
                state.copy(loading = false)
            })

            is MainMapIntent.SelectPhotoLocationMarker -> state.copy(
                cameraLocation = intent.targetPhoto.location,
                photoList = intent.photoList,
                targetPhoto = intent.targetPhoto,
                loading = false,
            )

            is MainMapIntent.SelectLocation -> state.apply {
                _effect.emit(MainMapEffect.NavigateToDetailLocationMap(intent.targetPhoto))
            }

            is MainMapIntent.SelectPhoto -> state.apply {
                _effect.emit(MainMapEffect.NavigateToPhoto(targetPhoto = intent.targetPhoto))
            }

            is MainMapIntent.ResponsePermissionRequest -> state.copy(
                loading = false,
                permissionState = state.permissionState.copy(
                    filePermission = intent.permissionState.filePermission.copy(),
                    locationPermission = intent.permissionState.locationPermission.copy()
                )
            )

            MainMapIntent.GoToAcceptPermission -> state.apply {
                _effect.emit(MainMapEffect.NavigateToAppSetting)
            }

            MainMapIntent.SearchAndMoveToCurrentLocation -> state.apply {
                _effect.emit(MainMapEffect.MoveToCurrentLocation)
            }

            is MainMapIntent.LookAroundCurrentLocation -> state.copy(
                cameraLocation = intent.location
            ).apply {
                // todo 데이터 베이스 정보 불러서 state에 넣기
            }
        }
    }
}