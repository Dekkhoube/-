package com.example.util

import java.util.Calendar

data class BengaliDate(
    val day: Int,
    val monthNameBengali: String,
    val monthIndex: Int, // 1 to 12
    val year: Int,
    val seasonBengali: String,
    val dayOfWeekBengali: String,
    val formatted: String
)

object BengaliCalendarHelper {

    val BENGALI_MONTHS = listOf(
        "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
        "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র"
    )

    val BENGALI_SEASONS = listOf(
        "গ্রীষ্মকাল", "বর্ষাকাল", "শরৎকাল", "হেমন্তকাল", "শীতকাল", "বসন্তকাল"
    )

    val BENGALI_DAYS_OF_WEEK = listOf(
        "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
    )

    fun getBengaliDate(calendar: Calendar = Calendar.getInstance()): BengaliDate {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) // 0-based: 0=Jan, 11=Dec
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun, 6=Sat

        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

        // Day counts in months from April 14
        // Jan: month=0, Feb: month=1, Mar: month=2, Apr: month=3...
        var bMonthIndex = 0
        var bDay = 1
        var bYear = if (month > 3 || (month == 3 && day >= 14)) year - 593 else year - 594

        when (month) {
            3 -> { // April
                if (day >= 14) {
                    bMonthIndex = 0 // Boishakh
                    bDay = day - 13
                } else {
                    bMonthIndex = 11 // Chaitra
                    bDay = day + 17
                }
            }
            4 -> { // May
                if (day <= 14) {
                    bMonthIndex = 0 // Boishakh
                    bDay = day + 17
                } else {
                    bMonthIndex = 1 // Jaishtha
                    bDay = day - 14
                }
            }
            5 -> { // June
                if (day <= 14) {
                    bMonthIndex = 1 // Jaishtha
                    bDay = day + 17
                } else {
                    bMonthIndex = 2 // Asharh
                    bDay = day - 14
                }
            }
            6 -> { // July
                if (day <= 15) {
                    bMonthIndex = 2 // Asharh
                    bDay = day + 16
                } else {
                    bMonthIndex = 3 // Srabon
                    bDay = day - 15
                }
            }
            7 -> { // August
                if (day <= 15) {
                    bMonthIndex = 3 // Srabon
                    bDay = day + 16
                } else {
                    bMonthIndex = 4 // Bhadro
                    bDay = day - 15
                }
            }
            8 -> { // September
                if (day <= 15) {
                    bMonthIndex = 4 // Bhadro
                    bDay = day + 16
                } else {
                    bMonthIndex = 5 // Ashwin
                    bDay = day - 15
                }
            }
            9 -> { // October
                if (day <= 15) {
                    bMonthIndex = 5 // Ashwin
                    bDay = day + 15
                } else {
                    bMonthIndex = 6 // Kartik
                    bDay = day - 15
                }
            }
            10 -> { // November
                if (day <= 14) {
                    bMonthIndex = 6 // Kartik
                    bDay = day + 16
                } else {
                    bMonthIndex = 7 // Ogrohayon
                    bDay = day - 14
                }
            }
            11 -> { // December
                if (day <= 14) {
                    bMonthIndex = 7 // Ogrohayon
                    bDay = day + 16
                } else {
                    bMonthIndex = 8 // Poush
                    bDay = day - 14
                }
            }
            0 -> { // January
                if (day <= 13) {
                    bMonthIndex = 8 // Poush
                    bDay = day + 17
                } else {
                    bMonthIndex = 9 // Magh
                    bDay = day - 13
                }
            }
            1 -> { // February
                if (day <= 12) {
                    bMonthIndex = 9 // Magh
                    bDay = day + 18
                } else {
                    bMonthIndex = 10 // Falgun
                    bDay = day - 12
                }
            }
            2 -> { // March
                val falgunCutoff = if (isLeapYear) 15 else 14
                if (day <= falgunCutoff) {
                    bMonthIndex = 10 // Falgun
                    bDay = day + (if (isLeapYear) 17 else 16)
                } else {
                    bMonthIndex = 11 // Chaitra
                    bDay = day - falgunCutoff
                }
            }
        }

        val monthName = BENGALI_MONTHS[bMonthIndex]
        val season = BENGALI_SEASONS[bMonthIndex / 2]
        val dayOfWeek = BENGALI_DAYS_OF_WEEK.getOrElse(dayOfWeekIndex.coerceIn(0, 6)) { "" }

        val bDayBengali = convertToBengaliDigits(bDay)
        val bYearBengali = convertToBengaliDigits(bYear)

        return BengaliDate(
            day = bDay,
            monthNameBengali = monthName,
            monthIndex = bMonthIndex + 1,
            year = bYear,
            seasonBengali = season,
            dayOfWeekBengali = dayOfWeek,
            formatted = "$bDayBengali $monthName $bYearBengali বঙ্গাব্দ ($season)"
        )
    }

    fun convertToBengaliDigits(number: Int): String {
        val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        val numStr = number.toString()
        for (ch in numStr) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }
}
