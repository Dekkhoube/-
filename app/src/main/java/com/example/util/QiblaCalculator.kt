package com.example.util

import kotlin.math.*

object QiblaCalculator {
    // Kaaba Coordinates in Mecca
    const val KAABA_LATITUDE = 21.422487
    const val KAABA_LONGITUDE = 39.826206
    private const val EARTH_RADIUS_KM = 6371.0

    /**
     * Calculates the forward azimuth / bearing from the observer's location to the Kaaba in degrees.
     * Returns a value between 0.0 and 360.0 degrees clockwise from True North.
     */
    fun calculateQiblaBearing(latitude: Double, longitude: Double): Float {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LATITUDE)
        val deltaLambda = Math.toRadians(KAABA_LONGITUDE - longitude)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)

        var bearing = Math.toDegrees(atan2(y, x))
        if (bearing < 0) {
            bearing += 360.0
        }
        return bearing.toFloat()
    }

    /**
     * Calculates great circle distance to Mecca in kilometers.
     */
    fun calculateDistanceToKaabaKm(latitude: Double, longitude: Double): Double {
        val dLat = Math.toRadians(KAABA_LATITUDE - latitude)
        val dLon = Math.toRadians(KAABA_LONGITUDE - longitude)
        val a = sin(dLat / 2.0).pow(2.0) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(KAABA_LATITUDE)) * sin(dLon / 2.0).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return EARTH_RADIUS_KM * c
    }
}
