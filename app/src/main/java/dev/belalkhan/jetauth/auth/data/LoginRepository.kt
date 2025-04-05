package dev.belalkhan.jetauth.auth.data

import dev.belalkhan.jetauth.data.network.NetworkResult

interface LoginRepository {
    suspend fun login(username: String, password: String): NetworkResult<LoginResponse>

    suspend fun user(): NetworkResult<LoginResponse>
}