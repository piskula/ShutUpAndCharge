package org.momosi.shutupandcharge.core.common.coroutines

import kotlinx.coroutines.CancellationException

@Suppress("TooGenericExceptionCaught")
inline fun <R> runCatchingCancellable(block: () -> R): Result<R> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}

inline fun <R, T> Result<T>.mapCatchingCancellable(transform: (value: T) -> R): Result<R> = when {
    isSuccess -> runCatchingCancellable { transform(getOrThrow()) }
    else -> Result.failure(exceptionOrNull()!!)
}
