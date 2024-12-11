package ny.photomap.domain

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val throwable: Throwable?) : Result<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isFailure(): Boolean = this is Failure

    fun getOrNull(): T? = if (this is Success) this.data else null
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        block(this.data)
    }
    return this
}

inline fun <T> Result<T>.onFailure(
    block: (Throwable?) -> Unit,
): Result<T> {
    if (this is Result.Failure) {
        block(this.throwable)
    }
    return this
}

fun <T> Result<T>.onResponse(onSuccess: (T) -> Unit, onFailure: (Throwable?) -> Unit) {
    this.onSuccess {
        onSuccess(it)
    }.onFailure {
        onFailure(it)
    }
}


fun <R, T> Result<T>.map(transform: (T) -> R): Result<R> {
    return if (this is Result.Success) runResultCatching { transform(this.data) }
    else Result<R>.Failure((this as Result.Failure).throwable)
}

fun <R, T> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return if (this is Result.Success) transform(this.data)
    else Result<R>.Failure((this as Result.Failure).throwable)
}

suspend fun <R, T> Result<T>.suspendMap(transform: suspend (T) -> R): Result<R> {
    return if (this is Result.Success) runResultCatching { transform(this.data) }
    else Result<R>.Failure((this as Result.Failure).throwable)
}

suspend fun <R, T> Result<T>.suspendFlatMap(transform: suspend (T) -> Result<R>): Result<R> {
    return if (this is Result.Success) try {
        transform(this.data)
    } catch (e: Throwable) {
        Result<R>.Failure(e)
    }
    else Result<R>.Failure((this as Result.Failure).throwable)
}

inline fun <R> runResultCatching(block: () -> R): Result<R> {
    return try {
        Result.Success(block())
    } catch (e: Throwable) {
        Result.Failure(e)
    }
}