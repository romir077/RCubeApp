package com.rcube.app.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApiException(message: String) : Exception(message)

/**
 * Thin RPC client for the Apps Script Web App. Everything is a POST of
 * `{ action, apiKey, token, data }` and returns the `data` field of the envelope.
 * OkHttp follows the 302 redirect that Apps Script issues to its content URL.
 */
class RcubeApi(
    private val baseUrl: String,
    private val apiKey: String,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    private suspend fun rpc(action: String, token: String?, data: JsonObject): JsonElement =
        withContext(Dispatchers.IO) {
            val payload = buildJsonObject {
                put("action", action)
                put("apiKey", apiKey)
                if (token != null) put("token", token)
                put("data", data)
            }
            val request = Request.Builder()
                .url(baseUrl)
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            client.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (text.isEmpty()) throw ApiException("Empty response from server")
                val envelope = runCatching { json.parseToJsonElement(text).jsonObject }
                    .getOrElse { throw ApiException("Unexpected response: ${text.take(180)}") }
                val ok = envelope["ok"]?.jsonPrimitive?.content == "true"
                if (!ok) {
                    val err = envelope["error"]?.jsonPrimitive?.content ?: "Request failed"
                    throw ApiException(err)
                }
                envelope["data"] ?: JsonNull
            }
        }

    private inline fun <reified T> decode(element: JsonElement): T =
        json.decodeFromJsonElement(element)

    suspend fun ping(): Boolean =
        runCatching { rpc("ping", null, JsonObject(emptyMap())) }.isSuccess

    suspend fun authVerify(phone: String, otp: String): AuthDto =
        decode(rpc("authVerify", null, buildJsonObject {
            put("phone", phone); put("otp", otp)
        }))

    suspend fun getState(token: String): StateDto =
        decode(rpc("getState", token, JsonObject(emptyMap())))

    suspend fun searchCreators(token: String, category: String, radiusKm: Int): List<ProfileDto> =
        decode(rpc("searchCreators", token, buildJsonObject {
            put("category", category); put("radiusKm", radiusKm)
        }))

    suspend fun createProfile(
        token: String,
        displayName: String,
        category: String,
        bio: String,
        city: String,
        languages: List<String>,
    ): ProfileDto = decode(rpc("createProfile", token, buildJsonObject {
        put("displayName", displayName)
        put("category", category)
        put("bio", bio)
        put("city", city)
        putJsonArray("languages") { languages.forEach { add(it) } }
    }))

    suspend fun addService(
        token: String,
        profileId: String,
        title: String,
        pricePaise: Long,
        durationMinutes: Int?,
        description: String?,
    ): ProfileDto = decode(rpc("addService", token, buildJsonObject {
        put("profileId", profileId)
        put("title", title)
        put("pricePaise", pricePaise)
        if (durationMinutes != null) put("durationMinutes", durationMinutes)
        if (description != null) put("description", description)
    }))

    suspend fun submitProfile(token: String, profileId: String): ProfileDto =
        decode(rpc("submitProfile", token, buildJsonObject { put("profileId", profileId) }))

    suspend fun createBooking(
        token: String,
        creatorProfileId: String,
        serviceId: String,
        eventDate: String,
        eventType: String,
        venue: String,
        notes: String,
    ): BookingDto = decode(rpc("createBooking", token, buildJsonObject {
        put("creatorProfileId", creatorProfileId)
        put("serviceId", serviceId)
        put("eventDate", eventDate)
        put("eventType", eventType)
        put("venue", venue)
        put("notes", notes)
    }))

    suspend fun acceptRequest(token: String, bookingId: String): BookingDto =
        decode(rpc("acceptRequest", token, buildJsonObject { put("bookingId", bookingId) }))

    suspend fun declineRequest(token: String, bookingId: String): BookingDto =
        decode(rpc("declineRequest", token, buildJsonObject { put("bookingId", bookingId) }))

    suspend fun payAndConfirm(token: String, bookingId: String): BookingDto =
        decode(rpc("payAndConfirm", token, buildJsonObject { put("bookingId", bookingId) }))

    suspend fun startEventWithOtp(token: String, bookingId: String, otp: String): BookingDto =
        decode(rpc("startEventWithOtp", token, buildJsonObject {
            put("bookingId", bookingId); put("otp", otp)
        }))

    suspend fun completeBooking(token: String, bookingId: String): BookingDto =
        decode(rpc("completeBooking", token, buildJsonObject { put("bookingId", bookingId) }))

    suspend fun cancelBooking(token: String, bookingId: String): BookingDto =
        decode(rpc("cancelBooking", token, buildJsonObject { put("bookingId", bookingId) }))

    suspend fun markNotificationsRead(token: String) {
        rpc("markNotificationsRead", token, JsonObject(emptyMap()))
    }

    private companion object {
        val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    }
}
