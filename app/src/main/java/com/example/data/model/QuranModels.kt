package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameBengali: String,
    val nameEnglish: String,
    val englishMeaning: String,
    val totalVerses: Int,
    val revelationType: String, // "মক্কী" or "মাদানী"
    val startingJuz: Int,
    val fullAudioUrl: String = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee/${String.format("%03d", number)}.mp3"
)

data class Ayah(
    val numberInQuran: Int,
    val surahNumber: Int,
    val numberInSurah: Int,
    val textArabic: String,
    val textBengali: String,
    val transliteration: String,
    val audioUrl: String,
    val juzNumber: Int,
    val page: Int = 1
)

data class JuzInfo(
    val number: Int,
    val nameArabic: String,
    val nameBengali: String,
    val startingSurahNumber: Int,
    val startingSurahNameBengali: String,
    val startingAyahNumber: Int,
    val endingSurahNameBengali: String = "",
    val endingAyahNumber: Int = 0,
    val surahRangeBengali: String = "",
    val audioUrl: String = "https://everyayah.com/data/Alafasy_128kbps/${String.format("%03d", startingSurahNumber)}${String.format("%03d", startingAyahNumber)}.mp3"
)

data class ReciterInfo(
    val id: String,
    val nameBengali: String,
    val nameEnglish: String,
    val audioBaseUrl: String
)
