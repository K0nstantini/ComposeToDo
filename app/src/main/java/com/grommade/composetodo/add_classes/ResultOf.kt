package com.grommade.composetodo.add_classes

sealed class ResultOf<out T> {
    data class Success<out R>(val value: R): ResultOf<R>()
    data class Failure(
        val message: String?,
        val throwable: Throwable?
    ): ResultOf<Nothing>()
}

inline fun <reified T> ResultOf<T>.doIfFailure(callback: (error: String?, throwable: Throwable?) -> Unit) {
    if (this is ResultOf.Failure) {
        callback(message, throwable)
    }
}

inline fun <reified T> ResultOf<T>.doIfSuccess(callback: (value: T) -> Unit) {
    if (this is ResultOf.Success) {
        callback(value)
    }
}

// FIXME: WTF?
inline fun <reified T, reified R> ResultOf<T>.map(transform: (T) -> R): ResultOf<R> {
    return when (this) {
        is ResultOf.Success -> ResultOf.Success(transform(value))
        is ResultOf.Failure -> this
    }
}

inline fun <T> ResultOf<T>.withDefault(value: () -> T): ResultOf.Success<T> {
    return when (this) {
        is ResultOf.Success -> this
        is ResultOf.Failure -> ResultOf.Success(value())
    }
}