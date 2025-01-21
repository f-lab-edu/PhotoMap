package ny.photomap.ui.photo

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
import kotlinx.coroutines.withContext
import ny.photomap.BaseViewModel
import ny.photomap.domain.onResponse
import ny.photomap.domain.usecase.GetPhotoLocationUseCase
import ny.photomap.model.toPhotoLocationUiModel
import ny.photomap.ui.Destination
import timber.log.Timber
import javax.inject.Inject

sealed interface PhotoIntent {
    object LoadPhoto : PhotoIntent
    object GoBack : PhotoIntent
}

data class PhotoState(
    val location: String? = null,
    val dateTime: String? = null,
    val uri: Uri? = null,
)

sealed interface PhotoEffect {
    data class Error(@StringRes val message: Int) : PhotoEffect
}

@HiltViewModel
class PhotoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotoLocation: GetPhotoLocationUseCase,
) : BaseViewModel<PhotoIntent, PhotoState, PhotoEffect>() {

    private val photo = savedStateHandle.toRoute<Destination.Photo>()

    private val _effect = MutableSharedFlow<PhotoEffect>()
    override val effect: SharedFlow<PhotoEffect> = _effect

    override val intent = Channel<PhotoIntent>()

    override val state = intent.receiveAsFlow().runningFold(PhotoState()) { state, intent ->
        withContext(Dispatchers.Default) {
            reducer(state, intent)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PhotoState())

    override fun handleIntent(event: PhotoIntent) {
        viewModelScope.launch(Dispatchers.Default) {
            intent.send(event)
        }
    }

    override suspend fun reducer(state: PhotoState, intent: PhotoIntent): PhotoState {
        Timber.d("state: $state\nintent: $intent")
        return when (intent) {
            PhotoIntent.LoadPhoto -> {
                getPhotoLocation(id = photo.dataId).onResponse(
                    ifSuccess = { it ->
                        val model = it.toPhotoLocationUiModel()
                        PhotoState(
                            location = model.location.toString(),
                            dateTime = model.getDateText(),
                            uri = model.uri
                        )
                    },
                    ifFailure = {
                        _effect.emit(PhotoEffect.Error(message = ny.photomap.R.string.load_photo_list_fail))
                        state
                    }
                )
            }

            PhotoIntent.GoBack -> state
        }
    }


}