package com.rcube.app.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
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
import com.rcube.app.data.model.Service

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

    suspend fun setName(token: String, firstName: String, lastName: String): UserDto =
        decode(rpc("setName", token, buildJsonObject {
            put("firstName", firstName); put("lastName", lastName)
        }))

    suspend fun setUserPhoto(token: String, base64: String): UserDto =
        decode(rpc("setUserPhoto", token, buildJsonObject { put("base64", base64) }))

    suspend fun submitAadhaar(
        token: String,
        frontBase64: String?,
        backBase64: String?,
    ): UserDto =
        decode(rpc("submitAadhaar", token, buildJsonObject {
            if (frontBase64 != null) put("frontBase64", frontBase64)
            if (backBase64 != null) put("backBase64", backBase64)
        }))

    suspend fun searchCreators(
        token: String,
        category: String,
        radiusKm: Int,
        lat: Double?,
        lng: Double?,
    ): List<ProfileDto> =
        decode(rpc("searchCreators", token, buildJsonObject {
            put("category", category)
            put("radiusKm", radiusKm)
            if (lat != null) put("lat", lat)
            if (lng != null) put("lng", lng)
        }))

    suspend fun createProfile(
        token: String,
        displayName: String,
        category: String,
        bio: String,
        city: String,
        languages: List<String>,
        instagram: String?,
        youtube: String?,
        lat: Double?,
        lng: Double?,
        services: List<Service>,
    ): ProfileDto = decode(rpc("createProfile", token, buildJsonObject {
        put("displayName", displayName)
        put("category", category)
        put("bio", bio)
        put("city", city)
        putJsonArray("languages") { languages.forEach { add(it) } }
        put("instagram", instagram ?: "")
        put("youtube", youtube ?: "")
        if (lat != null) put("lat", lat)
        if (lng != null) put("lng", lng)
        putJsonArray("services") {
            services.forEach { s ->
                addJsonObject {
                    put("title", s.title)
                    put("pricePaise", s.pricePaise)
                    if (s.durationMinutes != null) put("durationMinutes", s.durationMinutes)
                    if (s.description != null) put("description", s.description)
                }
            }
        }
    }))

    suspend fun updateProfile(
        token: String,
        profileId: String,
        category: String,
        bio: String,
        city: String,
        languages: List<String>,
        instagram: String?,
        youtube: String?,
    ): ProfileDto = decode(rpc("updateProfile", token, buildJsonObject {
        put("profileId", profileId)
        put("category", category)
        put("bio", bio)
        put("city", city)
        putJsonArray("languages") { languages.forEach { add(it) } }
        put("instagram", instagram ?: "")
        put("youtube", youtube ?: "")
    }))

    suspend fun addPortfolioMedia(
        token: String,
        profileId: String,
        type: String,
        base64: String,
    ): ProfileDto = decode(rpc("addPortfolioMedia", token, buildJsonObject {
        put("profileId", profileId)
        put("type", type)
        put("base64", base64)
    }))

    suspend fun deletePortfolioMedia(token: String, profileId: String, slot: Int): ProfileDto =
        decode(rpc("deletePortfolioMedia", token, buildJsonObject {
            put("profileId", profileId); put("slot", slot)
        }))

    suspend fun setProfilePhoto(token: String, profileId: String, base64: String): ProfileDto =
        decode(rpc("setProfilePhoto", token, buildJsonObject {
            put("profileId", profileId); put("base64", base64)
        }))

    suspend fun setProfileActive(token: String, profileId: String, active: Boolean): ProfileDto =
        decode(rpc("setProfileActive", token, buildJsonObject {
            put("profileId", profileId); put("active", active)
        }))

    suspend fun deleteProfile(token: String, profileId: String) {
        rpc("deleteProfile", token, buildJsonObject { put("profileId", profileId) })
    }

    suspend fun updateService(
        token: String,
        profileId: String,
        serviceId: String,
        title: String,
        pricePaise: Long,
        durationMinutes: Int?,
        description: String?,
    ): ProfileDto = decode(rpc("updateService", token, buildJsonObject {
        put("profileId", profileId)
        put("serviceId", serviceId)
        put("title", title)
        put("pricePaise", pricePaise)
        if (durationMinutes != null) put("durationMinutes", durationMinutes)
        if (description != null) put("description", description)
    }))

    suspend fun deleteService(token: String, profileId: String, serviceId: String): ProfileDto =
        decode(rpc("deleteService", token, buildJsonObject {
            put("profileId", profileId); put("serviceId", serviceId)
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

    suspend fun completeBooking(
        token: String,
        bookingId: String,
        rating: Int,
        comment: String,
    ): BookingDto = decode(rpc("completeBooking", token, buildJsonObject {
        put("bookingId", bookingId)
        if (rating > 0) put("rating", rating)
        if (comment.isNotBlank()) put("comment", comment)
    }))

    suspend fun cancelBooking(token: String, bookingId: String): BookingDto =
        decode(rpc("cancelBooking", token, buildJsonObject { put("bookingId", bookingId) }))

    suspend fun markNotificationsRead(token: String) {
        rpc("markNotificationsRead", token, JsonObject(emptyMap()))
    }

    private companion object {
        val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    }
}
