package dev.belalkhan.jetauth.auth.data

import dev.belalkhan.jetauth.data.network.NetworkResult
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(): NetworkResult<LoginResponse> = repository.user()
}
