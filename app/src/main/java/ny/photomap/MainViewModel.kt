package ny.photomap


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ny.photomap.domain.onFailure
import ny.photomap.domain.onSuccess
import ny.photomap.domain.usecase.CheckSyncStateUseCase
import ny.photomap.domain.usecase.SyncPhotoUseCase
import ny.photomap.permission.MediaPermissionData
import ny.photomap.permission.readImagePermission
import javax.inject.Inject


sealed interface MainEvent {
    object RequestSync : MainEvent
    object SyncFinished : MainEvent
    object SyncFailed : MainEvent
    data class AcceptPermission(val syncStarted: Boolean) : MainEvent
    object Idle : MainEvent
}

data class MainState(
    val loading: Boolean = false,
    val error: String? = null,
    val requestPermission: MediaPermissionData? = null,
    val notice: String? = null,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkSyncState: CheckSyncStateUseCase,
    private val syncPhoto: SyncPhotoUseCase,
) : ViewModel() {

    private val events = Channel<MainEvent>()

    val state = events.receiveAsFlow()
        .runningFold(MainState(), ::reduceState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainState(loading = true))

    suspend fun onEvent(event: MainEvent) {
        events.send(event)
    }

    private suspend fun reduceState(current: MainState, event: MainEvent): MainState {
        return when (event) {
            is MainEvent.RequestSync -> if (shouldSync()) {
                current.copy(loading = false, requestPermission = readImagePermission)
            } else {
                current.copy(loading = false, requestPermission = null)
            }

            is MainEvent.AcceptPermission -> if (event.syncStarted) {
                viewModelScope.launch { // 순서.. SideEffect
                    syncPhotoStorage()
                }
                current.copy(loading = true, requestPermission = null)
            } else {
                current.copy(
                    loading = false,
                    requestPermission = null,
                    notice = ""
                )
            }

            is MainEvent.SyncFinished -> current.copy(loading = false, notice = "")

            is MainEvent.SyncFailed -> current.copy(loading = false, error = "실패")

            is MainEvent.Idle -> MainState()
        }
    }

    suspend fun shouldSync(): Boolean {
        val result = checkSyncState().onFailure {
            it?.printStackTrace()
        }
        return result.getOrNull()?.shouldSync == true
    }

    suspend fun syncPhotoStorage() {
        syncPhoto().onSuccess {
            onEvent(MainEvent.SyncFinished)
        }.onFailure {
            onEvent(MainEvent.SyncFailed)
        }
    }

}