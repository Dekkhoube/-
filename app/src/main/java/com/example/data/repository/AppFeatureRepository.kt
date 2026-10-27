package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlin.math.*

class AppFeatureRepository(private val database: AppDatabase) {

    val locationPresets = listOf(
        LocationPreset("ঢাকা", "Dhaka", 23.8103, 90.4125, 6.0),
        LocationPreset("চট্টগ্রাম", "Chittagong", 22.3569, 91.7832, 6.0),
        LocationPreset("সিলেট", "Sylhet", 24.8949, 91.8687, 6.0),
        LocationPreset("খুলনা", "Khulna", 22.8456, 89.5403, 6.0),
        LocationPreset("রাজশাহী", "Rajshahi", 24.3745, 88.6042, 6.0),
        LocationPreset("বরিশাল", "Barisal", 22.7010, 90.3535, 6.0),
        LocationPreset("রংপুর", "Rangpur", 25.7439, 89.2752, 6.0),
        LocationPreset("ময়মনসিংহ", "Mymensingh", 24.7471, 90.4203, 6.0),
        LocationPreset("কুমিল্লা", "Comilla", 23.4682, 91.1788, 6.0),
        LocationPreset("কক্সবাজার", "Cox's Bazar", 21.4272, 92.0058, 6.0),
        LocationPreset("পবিত্র মক্কা মুকাররমা", "Makkah", 21.4225, 39.8262, 3.0),
        LocationPreset("পবিত্র মদীনা মুনাওয়ারা", "Madinah", 24.4672, 39.6111, 3.0)
    )

    val defaultTasbihList = listOf(
        TasbihDhikrEntity("subhanallah", "سُبْحَانَ اللَّهِ", "সুবহানাল্লাহ", "SubhanAllah (আল্লাহ অতি পবিত্র)", 0, 33, 0),
        TasbihDhikrEntity("alhamdulillah", "الْحَمْدُ لِلَّهِ", "আলহামদুলিল্লাহ", "Alhamdulillah (যাবতীয় প্রশংসা আল্লাহর)", 0, 33, 0),
        TasbihDhikrEntity("allahuakbar", "اللَّهُ أَكْبَرُ", "আল্লাহু আকবার", "Allahu Akbar (আল্লাহ সর্বশ্রেষ্ঠ)", 0, 33, 0),
        TasbihDhikrEntity("astaghfirullah", "أَسْتَغْفِرُ اللَّهَ", "আস্তাগফিরুল্লাহ", "Astaghfirullah (আমি আল্লাহর কাছে ক্ষমা চাই)", 0, 100, 0),
        TasbihDhikrEntity("lailahaillallah", "لَا إِلَٰهَ إِلَّا اللَّهُ", "লা ইলাহা ইল্লাল্লাহ", "La ilaha illallah (আল্লাহ ছাড়া কোনো সত্য উপাস্য নেই)", 0, 100, 0),
        TasbihDhikrEntity("durood", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "সাল্লাল্লাহু আলাইহি ওয়া সাল্লাম", "Durood Sharif (আল্লাহর রাসূলের ওপর সালাত ও সালাম)", 0, 100, 0)
    )

    val bangladeshMosques = listOf(
        MosqueItem("1", "বায়তুল মোকাররম জাতীয় মসজিদ", "পল্টন, ঢাকা ১০০০", 23.7289, 90.4132, 0.0, true, "বাংলাদেশের জাতীয় মসজিদ, যা ১৯৬০ সালে প্রতিষ্ঠিত হয় এবং আধুনিক ইসলামিক স্থাপত্যের নিদর্শন।"),
        MosqueItem("2", "তারা মসজিদ (সিতারা মসজিদ)", "আরমানিটোলা, পুরান ঢাকা", 23.7147, 90.4019, 0.0, true, "মোঘল স্থাপত্যরীতি ও চীনা মাটির তারকাসদৃশ টুকরো দিয়ে সাজানো ঐতিহাসিক মসজিদ।"),
        MosqueItem("3", "ষাট গম্বুজ মসজিদ", "বাগেরহাট সদর, বাগেরহাট", 22.6744, 89.7422, 0.0, true, "ইউনেস্কো বিশ্ব ঐতিহ্যবাহী স্থান, যা পঞ্চদশ শতাব্দীতে হযরত খান জাহান আলী (রহ.) নির্মাণ করেন।"),
        MosqueItem("4", "চকবাজার শাহী মসজিদ", "চকবাজার, পুরান ঢাকা", 23.7188, 90.3951, 0.0, true, "১৬৭৬ সালে সুবাদার শায়েস্তা খাঁ কর্তৃক নির্মিত ঢাকার অন্যতম প্রাচীন ঐতিহাসিক মসজিদ।"),
        MosqueItem("5", "বাঘা শাহী মসজিদ", "বাঘা, রাজশাহী", 24.1919, 88.8353, 0.0, true, "১৫২৩ সালে সুলতান নাসিরুদ্দিন নসরত শাহ কর্তৃক নির্মিত টেরাকোটা অলংকরণের অনুপম নিদর্শন।"),
        MosqueItem("6", "হযরত শাহজালাল (রহ.) মাজার জামে মসজিদ", "দরগাহ গেট, সিলেট", 24.9038, 91.8677, 0.0, true, "সিলেটের আধ্যাত্মিক প্রাণকেন্দ্র ও ঐতিহাসিক দরগাহ মসজিদ।")
    )

    val bookmarks: Flow<List<BookmarkEntity>> = database.bookmarkDao().getAllBookmarks()
    val tasbihRecords: Flow<List<TasbihDhikrEntity>> = database.tasbihDao().getAllTasbihRecords()
    val errorReports: Flow<List<ErrorReportEntity>> = database.errorReportDao().getAllReports()
    val auditLogs: Flow<List<AuditLogEntity>> = database.auditLogDao().getAllLogs()
    val correctionProposals: Flow<List<ContentCorrectionEntity>> = database.correctionDao().getAllProposals()
    val adminContent: Flow<List<AdminContentEntity>> = database.adminContentDao().getAllContent()

    suspend fun addAdminContent(
        type: String,
        title: String,
        arabic: String = "",
        bengali: String = "",
        source: String = "",
        addedBy: String = ""
    ): Long {
        return database.adminContentDao().insertContent(
            AdminContentEntity(
                contentType = type,
                titleBengali = title,
                contentArabic = arabic,
                contentBengali = bengali,
                categoryOrSource = source,
                addedBy = addedBy
            )
        )
    }

    suspend fun deleteAdminContent(id: Long) {
        database.adminContentDao().deleteById(id)
    }

    fun getWorshipForDate(dateStr: String): Flow<DailyWorshipEntity?> = database.worshipDao().getWorshipForDate(dateStr)
    fun getRecentWorshipRecords(): Flow<List<DailyWorshipEntity>> = database.worshipDao().getRecentWorshipRecords()

    suspend fun saveWorship(worship: DailyWorshipEntity) {
        database.worshipDao().insertOrUpdateWorship(worship)
    }

    suspend fun addBookmark(type: String, refId: String, title: String, arabic: String, bengali: String, note: String = "") {
        database.bookmarkDao().insertBookmark(
            BookmarkEntity(
                type = type,
                referenceId = refId,
                titleBengali = title,
                arabicSnippet = arabic,
                bengaliSnippet = bengali,
                note = note
            )
        )
    }

    suspend fun removeBookmark(id: Long) {
        database.bookmarkDao().deleteById(id)
    }

    suspend fun updateTasbih(record: TasbihDhikrEntity) {
        database.tasbihDao().insertOrUpdate(record)
    }

    suspend fun submitErrorReport(
        contentType: String,
        recordId: String,
        issueCategory: String,
        explanation: String,
        referenceSource: String
    ): Long {
        return database.errorReportDao().insertReport(
            ErrorReportEntity(
                contentType = contentType,
                recordId = recordId,
                issueCategory = issueCategory,
                userExplanation = explanation,
                referenceSource = referenceSource
            )
        )
    }

    suspend fun updateErrorReportStatus(reportId: Long, newStatus: String, notes: String) {
        database.errorReportDao().updateReportStatus(reportId, newStatus, notes)
    }

    suspend fun createCorrectionProposal(
        contentType: String,
        recordId: String,
        fieldName: String,
        oldValue: String,
        proposedValue: String,
        authoritativeSource: String,
        submittedBy: String
    ): Long {
        val id = database.correctionDao().insertProposal(
            ContentCorrectionEntity(
                contentType = contentType,
                recordId = recordId,
                fieldName = fieldName,
                originalValue = oldValue,
                proposedValue = proposedValue,
                authoritativeSource = authoritativeSource,
                submittedBy = submittedBy,
                status = "SUBMITTED_FOR_REVIEW"
            )
        )
        // Record immutable audit log
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = submittedBy,
                role = "CONTENT_EDITOR",
                actionType = "PROPOSAL_SUBMITTED",
                contentType = contentType,
                recordId = recordId,
                fieldChanged = fieldName,
                oldValue = oldValue,
                newValue = proposedValue,
                reason = "সংশোধন প্রস্তাব উত্থাপন: $authoritativeSource"
            )
        )
        return id
    }

    suspend fun reviewCorrectionProposal(
        proposalId: Long,
        newStatus: String, // APPROVED, REJECTED, PUBLISHED
        reviewerEmail: String,
        reviewerRole: String,
        reason: String
    ) {
        database.correctionDao().updateProposalStatus(proposalId, newStatus, reviewerEmail)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = reviewerEmail,
                role = reviewerRole,
                actionType = newStatus,
                contentType = "CORRECTION_PROPOSAL",
                recordId = proposalId.toString(),
                fieldChanged = "status",
                oldValue = "SUBMITTED_FOR_REVIEW",
                newValue = newStatus,
                reason = reason
            )
        )
    }

    fun calculateZakat(
        cashInHand: Double,
        cashInBank: Double,
        goldGrams: Double,
        goldPricePerGram: Double,
        silverGrams: Double,
        silverPricePerGram: Double,
        businessStock: Double,
        receivables: Double,
        debts: Double,
        useSilverNisab: Boolean = true
    ): ZakatCalculation = Companion.calculateZakat(
        cashInHand, cashInBank, goldGrams, goldPricePerGram, silverGrams, silverPricePerGram,
        businessStock, receivables, debts, useSilverNisab
    )

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double =
        Companion.calculateDistance(lat1, lon1, lat2, lon2)

    companion object {
        // Zakat Calculation Engine
        fun calculateZakat(
            cashInHand: Double,
            cashInBank: Double,
            goldGrams: Double,
            goldPricePerGram: Double,
            silverGrams: Double,
            silverPricePerGram: Double,
            businessStock: Double,
            receivables: Double,
            debts: Double,
            useSilverNisab: Boolean = true
        ): ZakatCalculation {
            val goldValue = goldGrams * goldPricePerGram
            val silverValue = silverGrams * silverPricePerGram
            val totalAssets = cashInHand + cashInBank + goldValue + silverValue + businessStock + receivables
            val netZakatable = max(0.0, totalAssets - debts)

            // Standard Shariah Nisab thresholds:
            // Gold Nisab: 7.5 Tola = 87.48 grams
            // Silver Nisab: 52.5 Tola = 612.36 grams
            val silverNisabThreshold = 612.36 * silverPricePerGram
            val goldNisabThreshold = 87.48 * goldPricePerGram

            val selectedThreshold = if (useSilverNisab) silverNisabThreshold else goldNisabThreshold
            val isObligatory = netZakatable >= selectedThreshold
            val zakatPayable = if (isObligatory) netZakatable * 0.025 else 0.0

            return ZakatCalculation(
                totalWealthBdt = totalAssets,
                totalLiabilitiesBdt = debts,
                netZakatableBdt = netZakatable,
                nisabThresholdBdt = selectedThreshold,
                isZakatObligatory = isObligatory,
                zakatPayableBdt = zakatPayable,
                nisabBasis = if (useSilverNisab) "রূপার নেসাব (৫২.৫ তোলা / ৬১২.৩৬ গ্রাম)" else "স্বর্ণের নেসাব (৭.৫ তোলা / ৮৭.৪৮ গ্রাম)"
            )
        }

        fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return 6371.0 * c
        }
    }
}
