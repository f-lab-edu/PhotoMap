package ny.photomap

import androidx.lifecycle.ViewModel

abstract class BaseViewModel<Intent, State, Effect> : ViewModel(),
    MVIViewModel<Intent, State, Effect> {

    override fun onCleared() {
        super.onCleared()
        intent.cancel()
    }
}