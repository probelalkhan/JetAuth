package dev.belalkhan.jetauth.auth.data

import dev.belalkhan.jetauth.data.TokenManager
import dev.belalkhan.jetauth.data.network.NetworkResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val tokenManager: TokenManager,
) {
    suspend operator fun invoke(username: String, password: String): NetworkResult<LoginResponse> {
        val result = repository.login(username, password)
        if (result is NetworkResult.Success) {
            tokenManager.saveTokens(
                result.data.accessToken,
                result.data.refreshToken,
            )
        }
        return result
    }
}
