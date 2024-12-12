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
import ny.photomap.model.LocationModel
import ny.photomap.model.PhotoLocationUiModel
import javax.inject.Inject
import kotlin.collections.List

sealed interface MainMapIntent {

    // 싱크 타이밍 확인
    object CheckSyncTime : MainMapIntent

    // 싱크 진행
    object Sync : MainMapIntent

//    data class GoAppSettingToAcceptPermission(val permission: String) : MainMapIntent

    // 사진 마커 선택
    data class SelectPhotoLocationMarker(
        val photoList: List<String>,
        val targetPhoto: PhotoLocationUiModel,
    ) : MainMapIntent

    // 사진 위치 정보 선택
    data class SelectLocation(val targetPhoto: PhotoLocationUiModel) : MainMapIntent

    // 사진 선택
    data class SelectPhoto(val targetPhoto: PhotoLocationUiModel) : MainMapIntent

    data class ResponsePermissionRequest(val permissionState: AcceptPermissionState) : MainMapIntent

}

data class MainMapState(
    val cameraLocation: LocationModel = LocationModel(100.0, 100.0),
    val photoList: List<String> = emptyList(),
    val targetPhoto: PhotoLocationUiModel? = null,
    val loading: Boolean = false,
    val permissionNotice: Boolean = false,
    val permissionState: AcceptPermissionState = AcceptPermissionState(),
)

sealed interface MainMapEffect {
    object RequestImagePermission : MainMapEffect
    object NavigateToAppSetting : MainMapEffect
    data class NavigateToDetailLocationMap(val targetPhoto: PhotoLocationUiModel) : MainMapEffect
    data class NavigateToPhoto(val targetPhoto: PhotoLocationUiModel) : MainMapEffect
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
                    _effect.emit(MainMapEffect.RequestImagePermission)
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
        }
    }
}