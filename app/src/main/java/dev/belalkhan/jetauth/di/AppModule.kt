package dev.belalkhan.jetauth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.belalkhan.jetauth.BuildConfig
import dev.belalkhan.jetauth.data.network.http.JetAuthHttpClientBuilder
import io.ktor.client.HttpClient
import io.ktor.http.URLProtocol
import kotlinx.serialization.json.Json

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun providesJson(): Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Provides
    fun provideHttpClient(builder: JetAuthHttpClientBuilder): HttpClient = builder
        .protocol(URLProtocol.HTTPS)
        .host(BuildConfig.BASE_URL)
        .build()
}