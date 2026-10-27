package com.example.util

import com.example.data.model.HijriDate
import com.example.data.model.IslamicEvent
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

object HijriCalendarHelper {

    val HIJRI_MONTHS_BENGALI = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানী",
        "জমাদিউল আউয়াল", "জমাদিউস সানী", "রজব", "শাবান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
    )

    val HIJRI_MONTHS_ARABIC = listOf(
        "المحرّم", "صفر", "ربيع الأوّل", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجّة"
    )

    val MAJOR_ISLAMIC_EVENTS = listOf(
        IslamicEvent(1, 1, "পবিত্র হিজরি নববর্ষ", "ইসলামিক নতুন বর্ষের প্রথম দিন"),
        IslamicEvent(1, 10, "পবিত্র আশুরা", "১০ই মুহাররম - ঐতিহাসিক ও তাৎপর্যপূর্ণ দিন"),
        IslamicEvent(3, 12, "ঈদে মিলাদুন্নবী (সা.)", "১২ই রবিউল আউয়াল - বিশ্বনবী হযরত মুহাম্মদ (সা.) এর জন্ম ও ওফাত দিবস"),
        IslamicEvent(7, 27, "পবিত্র শবে মেরাজ", "২৭শে রজব - রাসুলুল্লাহ (সা.) এর ঊর্ধ্বাকাশ গমন"),
        IslamicEvent(8, 15, "পবিত্র শবে বরাত", "১৫ই শাবান - লাইলাতুল বরাত ও ভাগ্যরজনী"),
        IslamicEvent(9, 1, "রমজানুল মুবারক শুরু", "পবিত্র মাহে রমজানের প্রথম রোজা"),
        IslamicEvent(9, 27, "পবিত্র শবে কদর", "২৭শে রমজান - লাইলাতুল কদর ও হাজার মাসের চেয়ে শ্রেষ্ঠ রাত"),
        IslamicEvent(10, 1, "পবিত্র ঈদুল ফিতর", "১লা শাওয়াল - রোজার ঈদ ও আনন্দের দিন"),
        IslamicEvent(12, 9, "পবিত্র আরাফাহ দিবস", "৯ই জিলহজ্জ - হজ্জের প্রধান রুকন ও আরাফাতের ময়দানে অবস্থান"),
        IslamicEvent(12, 10, "পবিত্র ঈদুল আযহা", "১০ই জিলহজ্জ - কুরবানির ঈদ ও ত্যাগ-মহিমার দিন")
    )

    /**
     * Converts a Gregorian Calendar to Hijri Date using the astronomical algorithm,
     * applying user-configured moon sighting adjustment (-2 to +2 days).
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance(), dayAdjustment: Int = 0): HijriDate {
        val cal = calendar.clone() as Calendar
        if (dayAdjustment != 0) {
            cal.add(Calendar.DAY_OF_YEAR, dayAdjustment)
        }

        var year = cal.get(Calendar.YEAR)
        var month = cal.get(Calendar.MONTH) // 0-based
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Gregorian to Julian Day
        if (month < 2) {
            year -= 1
            month += 12
        }
        val a = floor(year / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (year + 4716)) + floor(30.6001 * (month + 1 + 1)) + day + b - 1524.5

        // Julian Day to Hijri
        val z = jd - 1948440 + 10632
        val n = floor((z - 1) / 10631.0)
        val z1 = z - 10631 * n + 354
        val j = (floor((10985 - z1) / 5316.0)) * (floor((50 * z1) / 17719.0)) +
                (floor(z1 / 5670.0)) * (floor((43 * z1) / 15238.0))
        val z2 = z1 - (floor((30 - j) / 15.0)) * (floor((17719 * j) / 50.0)) -
                (floor(j / 16.0)) * (floor((15238 * j) / 43.0)) + 29
        val m = floor((24 * z2) / 709.0).toInt()
        val d = (z2 - floor((709 * m) / 24.0)).toInt()
        val y = (30 * n + j - 30).toInt()

        val safeMonth = ((m - 1).coerceIn(0, 11))
        val monthNameBengali = HIJRI_MONTHS_BENGALI[safeMonth]
        val monthNameArabic = HIJRI_MONTHS_ARABIC[safeMonth]

        val upcomingEvent = MAJOR_ISLAMIC_EVENTS.find { it.hijriMonth == (safeMonth + 1) && it.hijriDay == d }

        val gregorianFormatted = String.format(
            Locale.US,
            "%02d %s %d",
            cal.get(Calendar.DAY_OF_MONTH),
            getMonthNameBengali(cal.get(Calendar.MONTH)),
            cal.get(Calendar.YEAR)
        )

        return HijriDate(
            day = d,
            monthNumber = safeMonth + 1,
            monthNameArabic = monthNameArabic,
            monthNameBengali = monthNameBengali,
            year = y,
            gregorianFormatted = gregorianFormatted,
            upcomingEventBengali = upcomingEvent?.nameBengali
        )
    }

    private fun getMonthNameBengali(monthIndex: Int): String {
        val months = listOf(
            "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
            "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
        )
        return months.getOrElse(monthIndex) { "" }
    }
}
