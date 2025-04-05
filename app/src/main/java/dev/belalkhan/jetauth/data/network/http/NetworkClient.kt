package dev.belalkhan.jetauth.data.network.http

import dev.belalkhan.jetauth.data.network.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.path
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NetworkClient @Inject constructor(
    private val json: Json,
    private val httpClient: HttpClient,
) {
    suspend fun <RESPONSE> get(
        url: String,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        responseSerializer: KSerializer<RESPONSE>,
    ): NetworkResult<RESPONSE> =
        execute { request(HttpMethod.Get, url, queryParams, headers, null, responseSerializer) }

    suspend fun <REQUEST, RESPONSE> post(
        url: String,
        body: REQUEST? = null,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        responseSerializer: KSerializer<RESPONSE>,
    ): NetworkResult<RESPONSE> =
        execute { request(HttpMethod.Post, url, queryParams, headers, body, responseSerializer) }

    suspend fun <REQUEST, RESPONSE> put(
        url: String,
        body: REQUEST? = null,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        responseSerializer: KSerializer<RESPONSE>,
    ): NetworkResult<RESPONSE> =
        execute { request(HttpMethod.Put, url, queryParams, headers, body, responseSerializer) }

    suspend fun <RESPONSE> delete(
        url: String,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        responseSerializer: KSerializer<RESPONSE>,
    ): NetworkResult<RESPONSE> =
        execute { request(HttpMethod.Delete, url, queryParams, headers, null, responseSerializer) }

    private suspend fun <T> execute(block: suspend () -> T): NetworkResult<T> {
        return try {
            NetworkResult.success(block())
        } catch (e: ClientRequestException) { // 4xx Errors
            val errorMessage = parseErrorMessage(e.response)
            NetworkResult.clientError(e.response.status, errorMessage)
        } catch (e: ServerResponseException) { // 5xx Errors
            NetworkResult.serverError(e.response.status, e.message)
        } catch (e: IOException) { // Network Errors
            NetworkResult.networkFailure("Network error occurred: ${e.message}")
        } catch (e: SerializationException) { // JSON Parsing Errors
            NetworkResult.parsingError("Failed to parse response JSON: ${e.message}")
        } catch (e: Exception) { // Other Unexpected Errors
            NetworkResult.unknownError("Unexpected error: ${e.message}")
        }
    }

    private suspend fun parseErrorMessage(response: HttpResponse): String {
        return try {
            val json = response.bodyAsText()
            Json.decodeFromString<ErrorResponse>(json).message
        } catch (_: Exception) {
            "Unknown client error (${response.status}): ${response.status.description}"
        }
    }

    private suspend fun <REQUEST, RESPONSE> request(
        method: HttpMethod,
        url: String,
        queryParams: Map<String, String>,
        headers: Map<String, String>,
        body: REQUEST?,
        responseSerializer: KSerializer<RESPONSE>,
    ): RESPONSE {
        val response: HttpResponse = httpClient.request {
            this.method = method
            headers.forEach { (key, value) -> header(key, value) }
            url {
                path(url)
                queryParams.forEach { (key, value) -> parameters.append(key, value) }
            }
            if (body != null) {
                setBody(body)
            }
        }
        val responseText = response.bodyAsText()
        return json.decodeFromString(responseSerializer, responseText)
    }
}

@Serializable
data class ErrorResponse(val message: String)