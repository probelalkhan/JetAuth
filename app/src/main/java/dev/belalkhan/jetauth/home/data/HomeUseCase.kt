package dev.belalkhan.jetauth.home.data

import dev.belalkhan.jetauth.data.TokenManager
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val tokenManager: TokenManager,
) {
    suspend fun logout() {
        tokenManager.clearTokens()
    }
}