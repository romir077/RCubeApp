package com.rcube.app.core.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Default search origin when the device location is unavailable (Bengaluru center). */
const val DEFAULT_LAT = 12.9716
const val DEFAULT_LNG = 77.5946

/** Great-circle distance in kilometres between two lat/lng points (Haversine). */
fun haversineKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2.0) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
}
