package com.example

import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicMethod
import com.example.data.repository.AppFeatureRepository
import com.example.data.repository.HadithAndDuaRepository
import com.example.data.repository.QuranRepository
import com.example.util.HijriCalendarHelper
import com.example.util.PrayerCalculator
import com.example.util.QiblaCalculator
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar
import kotlin.math.roundToInt

class ExampleUnitTest {

    @Test
    fun testQuran114SurahsCount() {
        val surahs = QuranRepository.all114Surahs
        assertEquals(114, surahs.size)
        assertEquals("আল-ফাতিহা", surahs.first().nameBengali)
        assertEquals(1, surahs.first().number)
        assertEquals("আন-নাস", surahs.last().nameBengali)
        assertEquals(114, surahs.last().number)
    }

    @Test
    fun testQuran30JuzCount() {
        val juzList = QuranRepository.all30Juz
        assertEquals(30, juzList.size)
        assertEquals(1, juzList.first().number)
        assertEquals(30, juzList.last().number)
    }

    @Test
    fun testSurahAlFatihahAyahs() {
        val ayahs = QuranRepository.getAyahsForSurah(1)
        assertEquals(7, ayahs.size)
        assertTrue(ayahs.first().textArabic.contains("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"))
        assertTrue(ayahs.first().textBengali.contains("পরম করুণাময়"))
    }

    @Test
    fun testQiblaBearingFromDhaka() {
        // Dhaka coordinates: 23.8103° N, 90.4125° E
        val bearing = QiblaCalculator.calculateQiblaBearing(23.8103, 90.4125)
        // From Bangladesh, Qibla is 277.55° -> 278° (West-Northwest)
        assertEquals(278, bearing.roundToInt())

        val distance = QiblaCalculator.calculateDistanceToKaabaKm(23.8103, 90.4125)
        assertTrue("Distance to Mecca should be ~5000-5200 km", distance in 5000.0..5300.0)
    }

    @Test
    fun testPrayerCalculationDhaka() {
        val cal = Calendar.getInstance()
        val schedule = PrayerCalculator.calculateTimes(
            calendar = cal,
            latitude = 23.8103,
            longitude = 90.4125,
            timezoneOffset = 6.0,
            method = CalculationMethod.KARACHI,
            juristic = JuristicMethod.HANAFI
        )

        assertNotNull(schedule)
        assertTrue(schedule.fajr.isNotBlank())
        assertTrue(schedule.sunrise.isNotBlank())
        assertTrue(schedule.dhuhr.isNotBlank())
        assertTrue(schedule.asr.isNotBlank())
        assertTrue(schedule.maghrib.isNotBlank())
        assertTrue(schedule.isha.isNotBlank())
        assertTrue(schedule.nextPrayerNameBengali.isNotBlank())
    }

    @Test
    fun testZakatCalculation() {
        val result = AppFeatureRepository.calculateZakat(
            cashInHand = 200000.0,
            cashInBank = 300000.0,
            goldGrams = 0.0,
            goldPricePerGram = 11500.0,
            silverGrams = 0.0,
            silverPricePerGram = 180.0,
            businessStock = 0.0,
            receivables = 0.0,
            debts = 50000.0,
            useSilverNisab = true
        )

        assertEquals(450000.0, result.netZakatableBdt, 0.01)
        assertTrue(result.isZakatObligatory)
        assertEquals(11250.0, result.zakatPayableBdt, 0.01)
    }

    @Test
    fun testHijriCalendarConversion() {
        val cal = Calendar.getInstance()
        val hijriDate = HijriCalendarHelper.getHijriDate(cal, 0)
        assertNotNull(hijriDate)
        assertTrue(hijriDate.year in 1445..1455)
        assertTrue(hijriDate.day in 1..30)
        assertTrue(hijriDate.monthNumber in 1..12)
        assertTrue(hijriDate.monthNameBengali.isNotBlank())
    }

    @Test
    fun testHadithAndDuaData() {
        val collections = HadithAndDuaRepository.collections
        assertTrue(collections.isNotEmpty())
        assertTrue(collections.any { it.id == "bukhari" })

        val hadiths = HadithAndDuaRepository.verifiedHadiths
        assertTrue(hadiths.isNotEmpty())
        assertTrue(hadiths.first().grade == "সহীহ")

        val duas = HadithAndDuaRepository.verifiedDuas
        assertTrue(duas.isNotEmpty())
    }
}
