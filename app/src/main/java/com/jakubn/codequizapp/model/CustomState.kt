package com.jakubn.codequizapp.model

sealed class CustomState<out T> {
    data class Success<out R>(val result: R): CustomState<R>()
    data class Failure(val message: String?): CustomState<Nothing>()
    object Idle: CustomState<Nothing>()
    object Loading: CustomState<Nothing>()
}