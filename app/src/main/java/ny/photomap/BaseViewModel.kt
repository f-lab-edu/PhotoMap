package ny.photomap

import androidx.lifecycle.ViewModel



abstract class BaseViewModel<Intent : MVIIntent, State : MVIState, Effect: MVIEffect> : ViewModel(),
    MVIViewModel<Intent, State, Effect> {

    override fun onCleared() {
        super.onCleared()
        intent.cancel()
    }
}