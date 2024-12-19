package ny.photomap

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
import ny.photomap.domain.usecase.GetLatestPhotoLocationUseCase
import ny.photomap.domain.usecase.SyncPhotoUseCase
import ny.photomap.model.LocationUIModel
import ny.photomap.model.PhotoLocationUIModel
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.List

sealed interface MainMapIntent {

    // 싱크 타이밍 확인
    object CheckSyncTime : MainMapIntent

    // 싱크 진행
    object Sync : MainMapIntent

    // 싱크 종료
    object SyncFinished : MainMapIntent

    // 파일 권한 거부
    object DenyFilePermission : MainMapIntent

    // 위치 권한 거부
    object DenyLocationPermission : MainMapIntent

    // 현재 위치로 이동
    object SearchCurrentLocation : MainMapIntent

    // 현재 위치로 이동해 지도의 사진들 보기
    data class LookAroundCurrentLocation(val location: LocationUIModel) : MainMapIntent

    // 사진 마커 선택
    data class SelectPhotoLocationMarker(
        val photoList: List<String>,
        val targetPhoto: PhotoLocationUIModel,
    ) : MainMapIntent

    // 사진 위치 정보 선택f
    data class SelectLocation(val targetPhoto: PhotoLocationUIModel) : MainMapIntent

    // 사진 선택
    data class SelectPhoto(val targetPhoto: PhotoLocationUIModel) : MainMapIntent


    // 권한 주겠다
    object GoToAcceptPermission : MainMapIntent
}

data class MainMapState(
    val cameraLocation: LocationUIModel = LocationUIModel(100.0, 100.0),
    val photoList: List<String> = emptyList(),
    val targetPhoto: PhotoLocationUIModel? = null,
    val loading: Boolean = false,
)

sealed interface MainMapEffect {
    object RequestPermissions : MainMapEffect
    object NavigateToAppSetting : MainMapEffect
    object MoveToCurrentLocation : MainMapEffect
    data class NavigateToDetailLocationMap(val targetPhoto: PhotoLocationUIModel) : MainMapEffect
    data class NavigateToPhoto(val targetPhoto: PhotoLocationUIModel) : MainMapEffect
    data class Notice(@StringRes val message: Int) : MainMapEffect
    data class Error(@StringRes val message: Int) : MainMapEffect
}

@HiltViewModel
class MainMapViewModel @Inject constructor(
    private val checkSyncState: CheckSyncStateUseCase,
    private val syncPhoto: SyncPhotoUseCase,
    private val getLatestPhotoLocation: GetLatestPhotoLocationUseCase,
) : ViewModel() {

    private val _effect = MutableSharedFlow<MainMapEffect>()
    val effect: SharedFlow<MainMapEffect> = _effect

    private val intent = Channel<MainMapIntent>()

    val state = intent.receiveAsFlow().runningFold(MainMapState(), ::reducer)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainMapState(loading = false))

    private var currentLocation: LocationUIModel? = null

    fun handleIntent(event: MainMapIntent) {
        viewModelScope.launch {
            intent.send(event)
        }
    }

    private suspend fun reducer(state: MainMapState, intent: MainMapIntent): MainMapState {
        Timber.d("state: $state\nintent: $intent")
        return when (intent) {
            MainMapIntent.CheckSyncTime -> checkSyncState()
                .onResponse(ifSuccess = {
                    Timber.d("싱크 진행을 위해 권한 요청")
                    Timber.d("싱크 진행 여부 : ${it.shouldSync}, 최근 업데이트 시간 : ${it.lastSyncTime}")
                    if(it.shouldSync) {
                        _effect.emit(MainMapEffect.RequestPermissions)
                    }

                    state
                }, ifFailure = {
                    Timber.d("싱크 없이 진행")
                    // todo
                    // 룸 데이터 긇어와서 화면에 노출
                    // getphoto
                    state
                })

            MainMapIntent.Sync -> {
                viewModelScope.launch(Dispatchers.Default) {
                    syncPhoto().onResponse(ifSuccess = {
                        Timber.d("싱크 완료")
                        //getphoto하고 노출
                        // 화면에 노출
                        // todo
                        _effect.emit(MainMapEffect.Notice(message = R.string.sync_update_complete))
                        handleIntent(MainMapIntent.SyncFinished)
                    }, ifFailure = {
                        it?.printStackTrace()
                        Timber.d("싱크 실패")
                        _effect.emit(MainMapEffect.Error(message = R.string.sync_update_fail))
                        handleIntent(MainMapIntent.SyncFinished)
                    })
                }

                state.copy(loading = true)
            }

            MainMapIntent.SyncFinished -> state.copy(loading = false)


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

            MainMapIntent.DenyFilePermission -> state

            MainMapIntent.DenyLocationPermission -> state.apply {
                getLatestPhotoLocation().onResponse(
                    ifSuccess = { latestData ->
                        latestData?.let {
                            _effect.emit(MainMapEffect.Notice(R.string.notice_move_to_latest_photo_location))
                            handleIntent(
                                MainMapIntent.LookAroundCurrentLocation(
                                    LocationUIModel(
                                        latitude = it.latitude,
                                        longitude = it.longitude
                                    )
                                )
                            )
                        } ?: run {
                            _effect.emit(MainMapEffect.Notice(R.string.notice_deny_location_permission))
                        }

                    }, ifFailure = {
                        it?.printStackTrace()
                        _effect.emit(MainMapEffect.Notice(R.string.notice_deny_location_permission))
                    }
                )
            }

            MainMapIntent.GoToAcceptPermission -> state.apply {
                _effect.emit(MainMapEffect.NavigateToAppSetting)
            }

            MainMapIntent.SearchCurrentLocation -> state.apply {
                _effect.emit(MainMapEffect.MoveToCurrentLocation)
            }

            is MainMapIntent.LookAroundCurrentLocation -> state.copy(
                cameraLocation = intent.location
            ).apply {
                currentLocation = intent.location
                // todo 데이터 베이스 정보 불러서 state에 넣기
            }
        }.apply {
            Timber.d("reducer result state : $this")
        }
    }

    override fun onCleared() {
        super.onCleared()
        intent.close()
    }
}