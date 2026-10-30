package com.example.data.model

data class HadithCollection(
    val id: String,
    val nameBengali: String,
    val nameArabic: String,
    val totalHadithCount: Int,
    val descriptionBengali: String
)

data class Hadith(
    val id: Int,
    val collectionId: String,
    val bookNumber: Int,
    val hadithNumber: Int,
    val chapterBengali: String,
    val narratorBengali: String,
    val textArabic: String,
    val transliteration: String = "",
    val textBengali: String,
    val grade: String, // "সহীহ", "হাসান"
    val reference: String
)

enum class DuaCategory(val displayNameBengali: String) {
    MORNING_EVENING("সকাল ও সন্ধ্যার জিকির"),
    PRAYER("নামাজ সংশ্লিষ্ট দোয়া"),
    SLEEP("ঘুমানো ও জাগ্রত হওয়া"),
    FOOD("খাবার ও পানীয়"),
    TRAVEL("সফর ও যানবাহন"),
    PROTECTION("বিপদ ও রোগমুক্তি"),
    RAMADAN("রমজান ও রোজা"),
    FORGIVENESS("তওবা ও ইস্তিগফার"),
    WUDU("অযু ও পবিত্রতা"),
    FAMILY("পিতা-মাতা ও পরিবার"),
    DAILY_LIFE("দৈনন্দিন জীবনের দোয়া")
}

data class DuaItem(
    val id: Int,
    val category: DuaCategory,
    val titleBengali: String,
    val textArabic: String,
    val transliteration: String,
    val meaningBengali: String,
    val reference: String,
    val recommendedCount: Int = 1,
    val virtues: String = ""
)

data class NamazStepItem(
    val stepNumber: Int,
    val title: String,
    val arabicText: String = "",
    val transliteration: String = "",
    val meaning: String = "",
    val instruction: String
)

data class WuduStepItem(
    val stepNumber: Int,
    val title: String,
    val arabicDua: String = "",
    val transliteration: String = "",
    val meaning: String = "",
    val instruction: String
)

data class NamazRakatSummary(
    val prayerName: String,
    val sunnahBefore: Int = 0,
    val fard: Int = 0,
    val sunnahAfter: Int = 0,
    val nafl: Int = 0,
    val witr: Int = 0,
    val total: Int
)
