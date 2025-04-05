package dev.belalkhan.jetauth.data.network.http

import dev.belalkhan.jetauth.data.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

class JetAuthHttpClientBuilder @Inject constructor(
    private val tokenManager: TokenManager,
    private val json: Json,
) {
    private var protocol: URLProtocol = URLProtocol.HTTP
    private lateinit var host: String
    private lateinit var client: HttpClient

    fun protocol(protocol: URLProtocol) = apply { this.protocol = protocol }

    fun host(host: String) = apply { this.host = host }

    fun build(): HttpClient {
        client = HttpClient(Android) {
            expectSuccess = true

            engine {
                connectTimeout = 5000
                socketTimeout = 5000
            }

            defaultRequest {
                url {
                    protocol = this@JetAuthHttpClientBuilder.protocol
                    host = this@JetAuthHttpClientBuilder.host
                }
                header(HttpHeaders.ContentType, "application/json")
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(Auth) {
                bearer {
                    // Load access token dynamically
                    loadTokens {
                        tokenManager.tokenData.firstOrNull()?.let { tokens ->
                            tokens.accessToken?.let { BearerTokens(it, tokens.refreshToken ?: "") }
                        }
                    }

                    // Refresh token logic
                    refreshTokens {
                        refreshToken()?.let { newAccessToken ->
                            BearerTokens(newAccessToken, "") // Refresh tokens
                        }
                    }
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
        }
        return client
    }

    private suspend fun refreshToken(): String? {
        val tokens = tokenManager.tokenData.firstOrNull() ?: return null
        val refreshToken = tokens.refreshToken ?: return null

        return try {
            val response: HttpResponse = client.post {
                url {
                    path("auth/refresh")
                }
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }

            if (response.status == HttpStatusCode.OK) {
                val tokenResponse = json.decodeFromString<TokenResponse>(response.bodyAsText())
                tokenManager.saveTokens(tokenResponse.accessToken, tokenResponse.refreshToken)
                tokenResponse.accessToken
            } else {
                tokenManager.clearTokens()
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tokenManager.clearTokens()
            null
        }
    }
}


@Serializable
data class TokenResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
)
