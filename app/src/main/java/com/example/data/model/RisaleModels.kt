package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data models for the Digital Library, Risale-i Nur Corpus, Lügat (Islamic Dictionary),
 * Thematic Q&A, and Wisdom Quotes.
 * Strictly non-video text content adhering to authorized open-access editions.
 */

data class RisaleBook(
    val id: String,
    val titleOriginal: String,
    val titleBengali: String,
    val titleEnglish: String,
    val author: String = "বদিউজ্জামান সাঈদ নুরসী (রহ.)",
    val category: String, // "প্রধান রচনাবলী", "ছোট পুস্তিকা", "যুব ও স্বাস্থ্য", "ধর্মতত্ত্ব"
    val descriptionBengali: String,
    val descriptionEnglish: String,
    val totalChapters: Int,
    val pagesCount: Int = 100,
    val coverAccentColorHex: Long = 0xFF1B5E20,
    val languageOptions: List<String> = listOf("বাংলা", "English", "Türkçe")
)

data class RisaleSection(
    val id: String,
    val sectionNumber: Int,
    val subHeadingBengali: String = "",
    val subHeadingEnglish: String = "",
    val textOriginal: String = "",
    val textBengali: String,
    val textEnglish: String,
    val quranicReference: String? = null,
    val hadithReference: String? = null
)

data class RisaleChapter(
    val id: String,
    val bookId: String,
    val chapterNumber: Int,
    val titleOriginal: String,
    val titleBengali: String,
    val titleEnglish: String,
    val introductionBengali: String = "",
    val sections: List<RisaleSection>
)

data class LugatWord(
    val id: String,
    val term: String,               // Term in Ottoman / Turkish / Arabic
    val pronunciationBengali: String, // Bengali phonetic pronunciation
    val pronunciationEnglish: String, // English phonetic pronunciation
    val originLanguage: String,     // e.g. "আরবি", "ফার্সি", "উসমানীয়"
    val meaningBengali: String,
    val meaningEnglish: String,
    val rootOrigin: String = "",
    val theologicalContextBengali: String = "",
    val theologicalContextEnglish: String = "",
    val sampleUsage: String = ""
)

data class RisaleThematicQuestion(
    val id: String,
    val topicBengali: String,
    val topicEnglish: String,
    val questionBengali: String,
    val questionEnglish: String,
    val answerSummaryBengali: String,
    val answerFullBengali: String,
    val answerFullEnglish: String,
    val sourceBookReference: String,
    val keyQuranVerses: List<String> = emptyList()
)

data class WisdomQuote(
    val id: String,
    val textBengali: String,
    val textEnglish: String,
    val textOriginal: String = "",
    val sourceBook: String,
    val category: String, // ঈমান, ধৈর্য, সৃষ্টিরহস্য, পরকাল, মোনাজাত
    val author: String = "বদিউজ্জামান সাঈদ নুরসী (রহ.)"
)

data class RisaleReadingGoal(
    val dailyTargetPages: Int = 10,
    val currentStreakDays: Int = 3,
    val totalPagesRead: Int = 45,
    val completedBooksCount: Int = 1,
    val lastReadBookId: String = "sozler",
    val lastReadChapterId: String = "sozler_ch_1",
    val lastReadChapterTitle: String = "প্রথম বাণী (বিসমিল্লাহর মাহাত্ম্য)"
)
