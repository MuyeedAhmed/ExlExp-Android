package com.muyeedahmed.exldroid.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackendApiClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Default URL: 10.0.2.2 maps to host localhost in Android Emulator
        // For physical device testing, use machine's local IP (e.g. 192.168.1.x) or AWS domain
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/api/v1"
        private const val PREFS_NAME = "exldroid_backend_prefs"
        private const val KEY_BASE_URL = "backend_base_url"
        private const val KEY_AUTH_TOKEN = "backend_auth_token"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        set(value) = prefs.edit().putString(KEY_BASE_URL, value.trimEnd('/')).apply()

    var authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_AUTH_TOKEN, value).apply()

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
    }

    suspend fun checkHealth(): Result<BackendHealthDto> = runCatching {
        val response = client.get("$baseUrl/health")
        if (response.status.isSuccess()) {
            response.body<BackendHealthDto>()
        } else {
            throw IllegalStateException("Health check failed with HTTP ${response.status.value}")
        }
    }

    suspend fun sync(pushRequest: SyncPushRequest): Result<SyncResponseDto> = runCatching {
        val response = client.post("$baseUrl/sync") {
            contentType(ContentType.Application.Json)
            authToken?.let { token ->
                header("Authorization", "Bearer $token")
            }
            setBody(pushRequest)
        }
        if (response.status.isSuccess()) {
            response.body<SyncResponseDto>()
        } else {
            throw IllegalStateException("Sync failed with HTTP ${response.status.value}")
        }
    }

    suspend fun login(loginReq: LoginRequestDto): Result<TokenResponseDto> = runCatching {
        val response = client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(loginReq)
        }
        if (response.status.isSuccess()) {
            val tokenRes = response.body<TokenResponseDto>()
            authToken = tokenRes.access_token
            tokenRes
        } else {
            throw IllegalStateException("Login failed with HTTP ${response.status.value}")
        }
    }

    suspend fun signUp(signUpReq: SignUpRequestDto): Result<TokenResponseDto> = runCatching {
        val response = client.post("$baseUrl/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(signUpReq)
        }
        if (response.status.isSuccess()) {
            // Automatically login after signup
            login(LoginRequestDto(username_or_email = signUpReq.username, password = signUpReq.password)).getOrThrow()
        } else {
            throw IllegalStateException("Sign up failed with HTTP ${response.status.value}")
        }
    }

    fun logout() {
        authToken = null
    }
}
