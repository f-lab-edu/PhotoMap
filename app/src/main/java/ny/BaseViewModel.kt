package ny

import androidx.lifecycle.ViewModel
import ny.photomap.domain.Result

class BaseViewModel : ViewModel() {

    fun <R, T : Result<R>> handleResult(
        result: Result<R>,
        onSuccess: (data: R) -> Unit = {},
        onFailure: (Throwable?) -> Unit = {},
    ) {
        when (result) {
            is Result.Success<R> -> onSuccess(result.data)
            is Result.Failure -> onFailure(result.throwable)
        }
    }

    fun <T> handleResult(
        result: T,
        onSuccess: (T) -> Unit = {},
        onFailure: (Throwable?) -> Unit = {},
    ) {
        try {
            onSuccess(result)
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}