package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data models for the comprehensive "ইসলামিক শিক্ষা ও আমল" (Islamic Knowledge & Daily Amal) section.
 * Covers:
 * 1. 26 Short Surahs (with per-ayah Arabic, pronunciation, translation, tafsir & audio)
 * 2. 7 Kalimas (Tayyibah, Shahadat, Tawhid, Tamjid, Iman-e-Mujmal, Iman-e-Mufassal, Radd-e-Kufr)
 * 3. 99 Names of Allah (Asma ul Husna with Arabic, pronunciation, meaning, explanation & reference)
 * 4. Durood Sharif (Ibrahim, Nariya, Tunajjina, Taj, Shifa, Mahi, Futuhat)
 * 5. Important Duas (23 Quranic & Sunnah duas from screenshots)
 * 6. Additional Islamic Guides (26 items: Janazah, Kasr, Masbuk, Kaza, Salatul Tasbih, Aqiqah, etc.)
 * 7. Salah Learning Center (5 prayers, Wudu, Ghusl, step-by-step positions, Sahw sajdah, Witr)
 * 8. Fasting & Ramadan (Sehri, Iftar, Tarawih niyat, dua & munajat)
 * 9. Special Nights & Eid Prayers (Shab-e-Qadr, Barat, Meraj, Eid-ul-Fitr, Eid-ul-Adha)
 */

enum class AmalCategoryType(
    val id: String,
    val titleBengali: String,
    val subtitleBengali: String,
    val itemCount: Int,
    val tagColorHex: Long = 0xFF059669
) {
    IMPORTANT_DUAS_23(
        id = "duas_23",
        titleBengali = "গুরুত্বপূর্ণ দো'আ (২৩টি)",
        subtitleBengali = "পবিত্র কুরআন ও হাদিসের শ্রেষ্ঠ রব্বানা ও মাসনুন দোয়াসমূহ",
        itemCount = 23,
        tagColorHex = 0xFF047857
    ),
    SHORT_SURAHS(
        id = "short_surahs",
        titleBengali = "২৬টি ছোট সূরা",
        subtitleBengali = "আরবি হরকত, বাংলা উচ্চারণ, অর্থ, অডিও ও তাফসীরসহ",
        itemCount = 26,
        tagColorHex = 0xFFB45309
    ),
    SEVEN_KALIMAS(
        id = "seven_kalimas",
        titleBengali = "সাতটি কালেমা",
        subtitleBengali = "ঈমান ও তাওহীদের পূর্ণাঙ্গ ও বিশুদ্ধ বাক্যসমূহ",
        itemCount = 7,
        tagColorHex = 0xFF1D4ED8
    ),
    ALLAH_NAMES(
        id = "allah_names",
        titleBengali = "আল্লাহর ৯৯টি নাম",
        subtitleBengali = "আসমাউল হুসনা ও ইসমে আজম (অর্থ, তাৎপর্য ও দলিল)",
        itemCount = 99,
        tagColorHex = 0xFFD97706
    ),
    DUROOD_SHARIF(
        id = "durood_sharif",
        titleBengali = "দরুদ শরিফ",
        subtitleBengali = "দরুদে ইব্রাহীম, নারিয়া, তুনাজ্জিনা, তাজ ও অন্যান্য",
        itemCount = 7,
        tagColorHex = 0xFF0D9488
    ),
    ADDITIONAL_AMAL_26(
        id = "additional_amal",
        titleBengali = "অতিরিক্ত আমল ও বিধান (২৬টি)",
        subtitleBengali = "আয়াতুল কুরসি, ইস্তিগফার, জানাজা, কসর, কাযা ও সালাতুল তাসবীহ",
        itemCount = 26,
        tagColorHex = 0xFF7C3AED
    ),
    NAMAZ_SHIKKHA(
        id = "namaz_shikkha",
        titleBengali = "নামাজ শিক্ষা ও নিয়ম",
        subtitleBengali = "৫ ওয়াক্ত নামাজের চিত্রসহ নিয়ম, অযু, গোসল ও নামাজের দোয়া",
        itemCount = 14,
        tagColorHex = 0xFF059669
    ),
    DAILY_ESSENTIAL_DUAS(
        id = "daily_duas",
        titleBengali = "দৈনন্দিন প্রয়োজনীয় দোয়া",
        subtitleBengali = "সকাল-সন্ধ্যা, ঘুম, আহার, সফর, পরিবার ও বিপদ মুক্তির দোয়া",
        itemCount = 35,
        tagColorHex = 0xFF2563EB
    ),
    JANAZAH_AND_GHUSL(
        id = "janazah_ghusl",
        titleBengali = "জানাজা ও মৃত ব্যক্তির গোসল",
        subtitleBengali = "দাফন-কাফনের নিয়ম, জানাজার তাকবীর ও মৃত ব্যক্তির দোয়া",
        itemCount = 8,
        tagColorHex = 0xFF4B5563
    ),
    RAMADAN_AND_ROZA(
        id = "ramadan_roza",
        titleBengali = "রোজা ও রমজানের আমল",
        subtitleBengali = "রোজার নিয়ত, ইফতারের দোয়া, তারাবি ও রমজানের আমল",
        itemCount = 10,
        tagColorHex = 0xFF059669
    ),
    TARAWIH_PRAYER(
        id = "tarawih",
        titleBengali = "তারাবির নামাজ ও মোনাজাত",
        subtitleBengali = "তারাবির নামাজের নিয়ত, দোয়া, মোনাজাত ও সুন্নাত বিধান",
        itemCount = 6,
        tagColorHex = 0xFFD97706
    ),
    EID_PRAYERS(
        id = "eid_prayers",
        titleBengali = "ঈদুল ফিতর ও ঈদুল আজহা",
        subtitleBengali = "ঈদের নামাজের নিয়ম, ৬ তাকবীর, খুতবা ও কুরবানির আমল",
        itemCount = 8,
        tagColorHex = 0xFF0284C7
    ),
    TAHAJJUD_AND_NAFL(
        id = "tahajjud_nafl",
        titleBengali = "তাহাজ্জুদ ও নফল নামাজ",
        subtitleBengali = "তাহাজ্জুদ, সালাতুল তাসবীহ, ইশরাক, চাশত ও আওয়াবিন",
        itemCount = 8,
        tagColorHex = 0xFF9333EA
    ),
    SPECIAL_NIGHTS(
        id = "special_nights",
        titleBengali = "লাইলাতুল কদর, বরাত ও মেরাজ",
        subtitleBengali = "শবে বরাত, শবে কদর ও শবে মেরাজের সঠিক নফল আমল",
        itemCount = 6,
        tagColorHex = 0xFF4338CA
    ),
    IMPORTANT_EDUCATION(
        id = "important_edu",
        titleBengali = "ইসলামিক গুরুত্বপূর্ণ শিক্ষা",
        subtitleBengali = "সাহু সিজদার পদ্ধতি, সূরা হাশর ও বাকারার শেষ আয়াতের ফজিলত",
        itemCount = 10,
        tagColorHex = 0xFFBE185D
    )
}

data class ShortSurahAyah(
    val ayahNumber: Int,
    val textArabic: String,
    val transliteration: String,
    val translationBengali: String,
    val tafsirNote: String = "",
    val audioUrl: String = ""
)

data class ShortSurah(
    val id: Int,
    val surahNumber: Int,
    val nameBengali: String,
    val nameArabic: String,
    val nameMeaning: String,
    val revelationType: String, // "মাক্কী" / "মাদানী"
    val totalVerses: Int,
    val virtues: String,
    val tafsirOverview: String,
    val fullSurahAudioUrl: String,
    val ayahs: List<ShortSurahAyah>
)

data class KalimaItem(
    val id: Int,
    val numberBengali: String,
    val titleBengali: String,
    val titleArabic: String,
    val textArabic: String,
    val transliteration: String,
    val translationBengali: String,
    val explanation: String,
    val virtues: String,
    val reference: String,
    val audioUrl: String = ""
)

data class AllahNameItem(
    val number: Int,
    val nameArabic: String,
    val pronunciationBengali: String,
    val meaningBengali: String,
    val explanationBengali: String,
    val quranReference: String,
    val audioUrl: String = "",
    val isFavorite: Boolean = false
)

data class DuroodItem(
    val id: Int,
    val numberBengali: String,
    val titleBengali: String,
    val textArabic: String,
    val transliteration: String,
    val translationBengali: String,
    val explanation: String,
    val virtues: String,
    val reference: String,
    val audioUrl: String = ""
)

data class IslamicTopicItem(
    val id: Int,
    val categoryId: String,
    val numberBengali: String = "",
    val titleBengali: String,
    val subtitleBengali: String = "",
    val textArabic: String = "",
    val transliteration: String = "",
    val translationBengali: String = "",
    val explanation: String = "",
    val procedureSteps: List<String> = emptyList(),
    val virtues: String = "",
    val reference: String = "",
    val audioUrl: String = "",
    val isFavorite: Boolean = false
)
