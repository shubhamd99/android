package com.shubhamtechie.calculator.core.common

sealed class ResultWrapper<out T> {
    data class Success<T>(val data: T) : ResultWrapper<T>()
    data class Error(val message: String) : ResultWrapper<Nothing>()
}
