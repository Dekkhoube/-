package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// --- Room Database Entities ---

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "QURAN", "HADITH", "DUA"
    val referenceId: String, // "surah:ayah" or hadith id or dua id
    val titleBengali: String,
    val arabicSnippet: String,
    val bengaliSnippet: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbih_records")
data class TasbihDhikrEntity(
    @PrimaryKey val id: String,
    val arabicText: String,
    val bengaliText: String,
    val transliteration: String,
    val currentCount: Int,
    val targetCount: Int,
    val totalCount: Int
)

@Entity(tableName = "daily_worship")
data class DailyWorshipEntity(
    @PrimaryKey val dateStr: String, // YYYY-MM-DD
    val fajr: Boolean = false,
    val dhuhr: Boolean = false,
    val asr: Boolean = false,
    val maghrib: Boolean = false,
    val isha: Boolean = false,
    val tahajjud: Boolean = false,
    val quranRecited: Boolean = false,
    val quranVersesCount: Int = 0,
    val fasting: Boolean = false,
    val tasbihDone: Boolean = false,
    val sadaqah: Boolean = false
)

@Entity(tableName = "error_reports")
data class ErrorReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentType: String, // "QURAN", "HADITH", "DUA", "AUDIO"
    val recordId: String,
    val issueCategory: String, // "ARABIC_TEXT", "BENGALI_TRANSLATION", "REFERENCE", "AUDIO_LINK"
    val userExplanation: String,
    val referenceSource: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, UNDER_REVIEW, RESOLVED, REJECTED
    val reviewerNotes: String = ""
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val adminEmail: String,
    val role: String,
    val actionType: String, // PROPOSED, APPROVED, REJECTED, PUBLISHED, ROLLBACK
    val contentType: String,
    val recordId: String,
    val fieldChanged: String,
    val oldValue: String,
    val newValue: String,
    val reason: String
)

@Entity(tableName = "content_corrections")
data class ContentCorrectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentType: String,
    val recordId: String,
    val fieldName: String,
    val originalValue: String,
    val proposedValue: String,
    val authoritativeSource: String,
    val submittedBy: String,
    val reviewedBy: String = "",
    val status: String = "DRAFT", // DRAFT, SUBMITTED_FOR_REVIEW, APPROVED, REJECTED, PUBLISHED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_content")
data class AdminContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contentType: String, // "NOTICE", "DUA", "HADITH", "MOSQUE", "EVENT"
    val titleBengali: String,
    val contentArabic: String = "",
    val contentBengali: String = "",
    val categoryOrSource: String = "",
    val addedBy: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// --- Non-Entity UI Models ---

data class HijriDate(
    val day: Int,
    val monthNumber: Int,
    val monthNameArabic: String,
    val monthNameBengali: String,
    val year: Int,
    val gregorianFormatted: String,
    val upcomingEventBengali: String? = null
)

data class IslamicEvent(
    val hijriMonth: Int,
    val hijriDay: Int,
    val nameBengali: String,
    val descriptionBengali: String
)

data class MosqueItem(
    val id: String,
    val nameBengali: String,
    val addressBengali: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double = 0.0,
    val isHistorical: Boolean = false,
    val detailsBengali: String = ""
)

data class ZakatCalculation(
    val totalWealthBdt: Double,
    val totalLiabilitiesBdt: Double,
    val netZakatableBdt: Double,
    val nisabThresholdBdt: Double,
    val isZakatObligatory: Boolean,
    val zakatPayableBdt: Double,
    val nisabBasis: String
)
