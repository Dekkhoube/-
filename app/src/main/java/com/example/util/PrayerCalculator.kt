package com.example.util

import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicMethod
import com.example.data.model.PrayerSchedule
import java.util.Calendar
import java.util.Locale
import kotlin.math.*

object PrayerCalculator {

    // Converts degrees to radians
    private fun degToRad(deg: Double): Double = deg * (Math.PI / 180.0)

    // Converts radians to degrees
    private fun radToDeg(rad: Double): Double = rad * (180.0 / Math.PI)

    // Normalizes an angle to 0..360
    private fun fixAngle(deg: Double): Double {
        var a = deg - 360.0 * floor(deg / 360.0)
        if (a < 0) a += 360.0
        return a
    }

    // Normalizes hours to 0..24
    private fun fixHour(h: Double): Double {
        var a = h - 24.0 * floor(h / 24.0)
        if (a < 0) a += 24.0
        return a
    }

    data class SunPosition(val declination: Double, val equationOfTime: Double)

    // Calculates sun declination and equation of time for a given Julian Day
    private fun calculateSun(julianDay: Double): SunPosition {
        val d = julianDay - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(degToRad(g)) + 0.020 * sin(degToRad(2 * g)))

        val e = 23.439 - 0.00000036 * d
        val ra = radToDeg(atan2(cos(degToRad(e)) * sin(degToRad(l)), cos(degToRad(l)))) / 15.0
        val fixedRa = fixHour(ra)

        val declination = radToDeg(asin(sin(degToRad(e)) * sin(degToRad(l))))
        val equationOfTime = (q / 15.0) - fixedRa

        return SunPosition(declination, equationOfTime)
    }

    private fun getJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    fun calculateTimes(
        calendar: Calendar = Calendar.getInstance(),
        latitude: Double,
        longitude: Double,
        timezoneOffset: Double,
        method: CalculationMethod = CalculationMethod.KARACHI,
        juristic: JuristicMethod = JuristicMethod.HANAFI
    ): PrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val jd = getJulianDay(year, month, day)
        val sun = calculateSun(jd)

        // Dhuhr is transit time
        val transit = fixHour(12.0 + timezoneOffset - (longitude / 15.0) - sun.equationOfTime)

        // Sunrise & Sunset (using standard angle 0.833 for atmospheric refraction)
        val sunAngle = 0.833
        val cosHourAngleSun = (sin(degToRad(-sunAngle)) - sin(degToRad(latitude)) * sin(degToRad(sun.declination))) /
                (cos(degToRad(latitude)) * cos(degToRad(sun.declination)))

        val sunriseHour: Double
        val sunsetHour: Double
        if (cosHourAngleSun in -1.0..1.0) {
            val halfDay = (1.0 / 15.0) * radToDeg(acos(cosHourAngleSun))
            sunriseHour = fixHour(transit - halfDay)
            sunsetHour = fixHour(transit + halfDay)
        } else {
            sunriseHour = 6.0
            sunsetHour = 18.0
        }

        // Fajr
        val cosHourAngleFajr = (sin(degToRad(-method.fajrAngle)) - sin(degToRad(latitude)) * sin(degToRad(sun.declination))) /
                (cos(degToRad(latitude)) * cos(degToRad(sun.declination)))
        val fajrHour = if (cosHourAngleFajr in -1.0..1.0) {
            fixHour(transit - (1.0 / 15.0) * radToDeg(acos(cosHourAngleFajr)))
        } else {
            fixHour(sunriseHour - 1.5)
        }

        // Asr calculation based on shadow factor
        val factor = juristic.shadowFactor.toDouble()
        val asrAngle = radToDeg(atan(1.0 / (factor + tan(degToRad(abs(latitude - sun.declination))))))
        val cosHourAngleAsr = (sin(degToRad(asrAngle)) - sin(degToRad(latitude)) * sin(degToRad(sun.declination))) /
                (cos(degToRad(latitude)) * cos(degToRad(sun.declination)))
        val asrHour = if (cosHourAngleAsr in -1.0..1.0) {
            fixHour(transit + (1.0 / 15.0) * radToDeg(acos(cosHourAngleAsr)))
        } else {
            fixHour(transit + 3.0)
        }

        // Maghrib is sunset
        val maghribHour = sunsetHour

        // Isha
        val ishaHour = if (method == CalculationMethod.UMM_AL_QURA) {
            fixHour(maghribHour + 1.5) // 90 mins after Maghrib
        } else {
            val cosHourAngleIsha = (sin(degToRad(-method.ishaAngle)) - sin(degToRad(latitude)) * sin(degToRad(sun.declination))) /
                    (cos(degToRad(latitude)) * cos(degToRad(sun.declination)))
            if (cosHourAngleIsha in -1.0..1.0) {
                fixHour(transit + (1.0 / 15.0) * radToDeg(acos(cosHourAngleIsha)))
            } else {
                fixHour(maghribHour + 1.5)
            }
        }

        val fajrStr = formatTime(fajrHour)
        val sunriseStr = formatTime(sunriseHour)
        val dhuhrStr = formatTime(transit)
        val asrStr = formatTime(asrHour)
        val maghribStr = formatTime(maghribHour)
        val ishaStr = formatTime(ishaHour)

        // Prohibited times
        // 1. Sunrise to 15 mins after
        val prohibitedSunriseEnd = formatTime(sunriseHour + 15.0 / 60.0)
        // 2. Zawal: 10 mins before Dhuhr until Dhuhr
        val prohibitedZawalStart = formatTime(transit - 12.0 / 60.0)
        // 3. Sunset: 15 mins before Maghrib until Maghrib
        val prohibitedSunsetStart = formatTime(sunsetHour - 15.0 / 60.0)

        // Next prayer countdown
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) / 60.0 + calendar.get(Calendar.SECOND) / 3600.0

        val prayerHours = listOf(
            Pair("ফজর", fajrHour),
            Pair("যোহর", transit),
            Pair("আসর", asrHour),
            Pair("মাগরিব", maghribHour),
            Pair("ইশা", ishaHour)
        )

        var nextName = "ফজর"
        var nextHour = fajrHour + 24.0 // tomorrow fajr by default
        var remainingHours = nextHour - currentHour

        for ((name, hour) in prayerHours) {
            if (hour > currentHour) {
                nextName = name
                nextHour = hour
                remainingHours = nextHour - currentHour
                break
            }
        }

        val remainingMillis = (remainingHours * 3600 * 1000).toLong()
        val nextTimeFormatted = formatTime(if (nextHour >= 24.0) nextHour - 24.0 else nextHour)

        return PrayerSchedule(
            fajr = fajrStr,
            sunrise = sunriseStr,
            dhuhr = dhuhrStr,
            asr = asrStr,
            maghrib = maghribStr,
            isha = ishaStr,
            nextPrayerNameBengali = nextName,
            nextPrayerTimeFormatted = nextTimeFormatted,
            remainingMillis = remainingMillis,
            prohibitedSunrise = "$sunriseStr - $prohibitedSunriseEnd",
            prohibitedZawal = "$prohibitedZawalStart - $dhuhrStr",
            prohibitedSunset = "$prohibitedSunsetStart - $maghribStr"
        )
    }

    fun formatTime(decimalHour: Double): String {
        val normalized = fixHour(decimalHour)
        val hours = normalized.toInt()
        val minutes = ((normalized - hours) * 60.0).roundToInt()
        var adjustedHours = hours
        var adjustedMinutes = minutes
        if (adjustedMinutes >= 60) {
            adjustedHours = (adjustedHours + 1) % 24
            adjustedMinutes = 0
        }

        val amPm = if (adjustedHours < 12) "AM" else "PM"
        val displayHour = when (adjustedHours % 12) {
            0 -> 12
            else -> adjustedHours % 12
        }

        return String.format(Locale.US, "%02d:%02d %s", displayHour, adjustedMinutes, amPm)
    }

    fun toBengaliDigits(input: String): String {
        val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
