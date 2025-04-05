package dev.belalkhan.jetauth.data.network

import io.ktor.http.HttpStatusCode

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    sealed class Error : NetworkResult<Nothing>() {
        data class ClientError(val statusCode: HttpStatusCode, val message: String?) : Error()
        data class ServerError(val statusCode: HttpStatusCode, val message: String?) : Error()
        data class NetworkFailure(val message: String) : Error()
        data class ParsingError(val message: String) : Error()
        data class UnknownError(val message: String) : Error()
    }

    companion object {
        fun <T> success(data: T): NetworkResult<T> = Success(data)
        fun clientError(statusCode: HttpStatusCode, message: String?) =
            Error.ClientError(statusCode, message)

        fun serverError(statusCode: HttpStatusCode, message: String?) =
            Error.ServerError(statusCode, message)

        fun networkFailure(message: String) = Error.NetworkFailure(message)
        fun parsingError(message: String) = Error.ParsingError(message)
        fun unknownError(message: String) = Error.UnknownError(message)
    }
}
