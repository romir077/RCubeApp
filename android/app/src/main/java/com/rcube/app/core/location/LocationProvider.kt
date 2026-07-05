package com.rcube.app.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/** A resolved device location. */
data class LatLng(val lat: Double, val lng: Double)

object LocationProvider {

    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission") // guarded by hasPermission()
    suspend fun current(context: Context): LatLng? {
        if (!hasPermission(context)) return null
        val client = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { cont ->
            try {
                client.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token,
                ).addOnSuccessListener { loc ->
                    cont.resume(loc?.let { LatLng(it.latitude, it.longitude) })
                }.addOnFailureListener {
                    cont.resume(null)
                }
            } catch (e: SecurityException) {
                cont.resume(null)
            }
        }
    }
}

/**
 * Drop-in effect: requests location permission (once) and reports the device location
 * via [onLocation]. Safe to place on any screen that needs the user's coordinates.
 */
@Composable
fun LocationEffect(onLocation: (LatLng) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            scope.launch { LocationProvider.current(context)?.let(onLocation) }
        }
    }

    LaunchedEffect(Unit) {
        if (LocationProvider.hasPermission(context)) {
            LocationProvider.current(context)?.let(onLocation)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}
