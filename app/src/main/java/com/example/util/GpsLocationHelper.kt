package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.model.LocationPreset
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object GpsLocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun getCurrentDeviceLocation(context: Context): LocationPreset? {
        if (!hasLocationPermission(context)) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null

        var bestLocation: Location? = null

        try {
            // Check GPS Provider
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLoc != null) bestLocation = gpsLoc
            }

            // Check Network Provider
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                val netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (netLoc != null && (bestLocation == null || netLoc.accuracy < bestLocation.accuracy)) {
                    bestLocation = netLoc
                }
            }

            // Check Passive Provider
            if (bestLocation == null && locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            }
        } catch (e: SecurityException) {
            Log.w("GpsLocationHelper", "Location permission denied: ${e.message}")
            return null
        } catch (e: Exception) {
            Log.e("GpsLocationHelper", "Error getting location: ${e.message}")
        }

        if (bestLocation == null) {
            return null
        }

        val lat = bestLocation.latitude
        val lon = bestLocation.longitude

        // Detect TimeZone Offset
        val tz = TimeZone.getDefault()
        val offsetHours = tz.rawOffset / (1000.0 * 60.0 * 60.0)

        // Try Geocoding city name or find nearest known district
        val cityName = resolveLocationName(context, lat, lon)

        return LocationPreset(
            nameBengali = cityName.first,
            nameEnglish = cityName.second,
            latitude = lat,
            longitude = lon,
            timezoneOffsetHours = offsetHours
        )
    }

    private fun resolveLocationName(context: Context, lat: Double, lon: Double): Pair<String, String> {
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val locality = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "আপনার এলাকা"
                    return Pair(locality, locality)
                }
            }
        } catch (e: Exception) {
            Log.d("GpsLocationHelper", "Geocoder fallback: ${e.message}")
        }

        // Nearest district fallback
        val districtPresets = listOf(
            Triple("ঢাকা", "Dhaka", Pair(23.8103, 90.4125)),
            Triple("চট্টগ্রাম", "Chittagong", Pair(22.3569, 91.7832)),
            Triple("সিলেট", "Sylhet", Pair(24.8949, 91.8687)),
            Triple("রাজশাহী", "Rajshahi", Pair(24.3745, 88.6042)),
            Triple("খুলনা", "Khulna", Pair(22.8456, 89.5403)),
            Triple("বরিশাল", "Barisal", Pair(22.7010, 90.3535)),
            Triple("রংপুর", "Rangpur", Pair(25.7439, 89.2752)),
            Triple("ময়মনসিংহ", "Mymensingh", Pair(24.7471, 90.4203)),
            Triple("কক্সবাজার", "Cox's Bazar", Pair(21.4272, 92.0058)),
            Triple("কুমিল্লা", "Comilla", Pair(23.4682, 91.1788)),
            Triple("বগুড়া", "Bogra", Pair(24.8465, 89.3777)),
            Triple("মক্কা মুকাররমা", "Mecca", Pair(21.3891, 39.8579)),
            Triple("মদিনা মুনাওয়ারা", "Medina", Pair(24.5247, 39.5692))
        )

        var closest = districtPresets.first()
        var minDistance = Double.MAX_VALUE

        for (item in districtPresets) {
            val dist = calculateDistance(lat, lon, item.third.first, item.third.second)
            if (dist < minDistance) {
                minDistance = dist
                closest = item
            }
        }

        return if (minDistance < 60.0) {
            Pair(closest.first, closest.second)
        } else {
            Pair(
                "জিপিএস অবস্থান (${String.format(Locale.US, "%.2f, %.2f", lat, lon)})",
                "GPS Location (${String.format(Locale.US, "%.2f, %.2f", lat, lon)})"
            )
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
