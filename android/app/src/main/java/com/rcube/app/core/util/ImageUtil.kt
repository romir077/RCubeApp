package com.rcube.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.rcube.app.data.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

/** A portfolio upload payload: media type + base64 body. */
data class UploadPayload(val type: MediaType, val base64: String)

/** Max raw video size we upload through the Apps Script backend. */
const val MAX_VIDEO_BYTES = 10 * 1024 * 1024 // ~10 MB

/**
 * Converts [uri] to an upload payload: images are downscaled + JPEG-compressed;
 * videos are uploaded raw if under [MAX_VIDEO_BYTES]. Returns null on failure / too large.
 */
suspend fun uriToUploadPayload(context: Context, uri: Uri): UploadPayload? =
    withContext(Dispatchers.IO) {
        val mime = context.contentResolver.getType(uri).orEmpty()
        if (mime.startsWith("video")) {
            val bytes = runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull() ?: return@withContext null
            if (bytes.size > MAX_VIDEO_BYTES) return@withContext null
            UploadPayload(MediaType.VIDEO, Base64.encodeToString(bytes, Base64.NO_WRAP))
        } else {
            val b64 = uriToBase64Jpeg(context, uri) ?: return@withContext null
            UploadPayload(MediaType.IMAGE, b64)
        }
    }

/**
 * Reads [uri], downscales the image so its longest side is <= [maxDim] px, re-encodes
 * it as JPEG at [quality], and returns base64 (NO_WRAP). Runs off the main thread.
 *
 * Kept intentionally small so uploads stay light; the backend saves the JPEG to Drive.
 */
suspend fun uriToBase64Jpeg(
    context: Context,
    uri: Uri,
    maxDim: Int = 1280,
    quality: Int = 70,
): String? = withContext(Dispatchers.IO) {
    runCatching {
        val resolver = context.contentResolver

        // Read dimensions first to compute a memory-friendly sample size.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val srcW = bounds.outWidth
        val srcH = bounds.outHeight
        if (srcW <= 0 || srcH <= 0) return@runCatching null

        var sample = 1
        while (max(srcW, srcH) / sample > maxDim) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }

        val decoded = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        } ?: return@runCatching null

        val scaled = scaleToMax(decoded, maxDim)
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
        if (scaled != decoded) scaled.recycle()
        decoded.recycle()

        Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }.getOrNull()
}

private fun scaleToMax(bitmap: Bitmap, maxDim: Int): Bitmap {
    val longest = max(bitmap.width, bitmap.height)
    if (longest <= maxDim) return bitmap
    val ratio = maxDim.toFloat() / longest
    return Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width * ratio).toInt().coerceAtLeast(1),
        (bitmap.height * ratio).toInt().coerceAtLeast(1),
        true,
    )
}
