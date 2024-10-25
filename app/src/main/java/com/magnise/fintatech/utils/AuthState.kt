package com.magnise.fintatech.utils

sealed class AuthState {
    data object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}