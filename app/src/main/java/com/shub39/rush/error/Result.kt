package com.shub39.rush.error

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: SourceError) : Result<Nothing>()
}