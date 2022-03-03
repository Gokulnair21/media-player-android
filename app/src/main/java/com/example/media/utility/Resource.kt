package com.example.media.utility

sealed class Resource<T>(val data: T? = null, val error: String? = null) {
    class Loading<T> : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(error: String? = null) : Resource<T>(error = error)

}


