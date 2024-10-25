package com.magnise.fintatech.domain.usecase

import com.magnise.fintatech.data.remote.api.AuthenticationManager

class LoginUseCase(private val authManager: AuthenticationManager) {

    suspend operator fun invoke(username: String, password: String): Result<Boolean> {
        return try {
            val success = authManager.getToken(username, password)
            if (success) {
                Result.success(true)
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
