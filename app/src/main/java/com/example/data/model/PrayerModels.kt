package com.example.data.model

enum class CalculationMethod(val displayNameBengali: String, val fajrAngle: Double, val ishaAngle: Double) {
    KARACHI("ইসলামিক ইউনিভার্সিটি করাচি (বাংলাদেশ ও ভারত)", 18.0, 18.0),
    UMM_AL_QURA("উম্মুল কুরা, মক্কা", 18.5, 90.0), // 90 min interval
    MUSLIM_WORLD_LEAGUE("রাবিতা আল-আলম আল-ইসলামী (MWL)", 18.0, 17.0),
    ISNA("ইসলামিক সোসাইটি অফ নর্থ আমেরিকা (ISNA)", 15.0, 15.0),
    EGYPT("মিশরীয় জেনারেল সার্ভে কর্তৃপক্ষ", 19.5, 17.5)
}

enum class JuristicMethod(val displayNameBengali: String, val shadowFactor: Int) {
    HANAFI("হানাফী (আসরের ছায়া দ্বিগুণ)", 2),
    SHAFI("শাফেয়ী / মালেকী / হাম্বলী (এক গুণ)", 1)
}

data class LocationPreset(
    val nameBengali: String,
    val nameEnglish: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double = 6.0
)

data class PrayerSchedule(
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val nextPrayerNameBengali: String,
    val nextPrayerTimeFormatted: String,
    val remainingMillis: Long,
    val prohibitedSunrise: String, // Sunrise to +15 mins
    val prohibitedZawal: String,   // 10 mins before Dhuhr
    val prohibitedSunset: String   // 15 mins before Maghrib
)

data class QiblaData(
    val qiblaBearing: Float,      // Target angle relative to True North
    val deviceHeading: Float,     // Current compass heading
    val relativeBearing: Float,   // Needle rotation
    val distanceKm: Double,       // Distance to Mecca
    val accuracy: String          // High, Medium, Low, Uncalibrated
)
