package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyWorshipEntity
import com.example.data.model.ErrorReportEntity
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.tasks.await

@IgnoreExtraProperties
data class FirestoreUserProfile(
    val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val currentLanguage: String = "bn",
    val selectedCity: String = "ঢাকা",
    val arabicFontSize: Double = 24.0,
    val bengaliFontSize: Double = 16.0,
    val isDarkMode: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreBookmark(
    val id: String = "",
    val userId: String = "",
    val type: String = "QURAN",
    val referenceId: String = "",
    val titleBengali: String = "",
    val arabicSnippet: String = "",
    val bengaliSnippet: String = "",
    val note: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

class FirebaseSyncRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing cloud Firestore.")
    }

    suspend fun syncUserProfile(
        displayName: String,
        currentLanguage: String,
        selectedCity: String,
        arabicFontSize: Float,
        bengaliFontSize: Float,
        isDarkMode: Boolean
    ) {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val docRef = db.collection("users").document(uid)

        try {
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                val data = mapOf(
                    "userId" to uid,
                    "email" to (user.email ?: ""),
                    "displayName" to displayName,
                    "currentLanguage" to currentLanguage,
                    "selectedCity" to selectedCity,
                    "arabicFontSize" to arabicFontSize.toDouble(),
                    "bengaliFontSize" to bengaliFontSize.toDouble(),
                    "isDarkMode" to isDarkMode,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(data).await()
            } else {
                val updateData = mapOf(
                    "displayName" to displayName,
                    "currentLanguage" to currentLanguage,
                    "selectedCity" to selectedCity,
                    "arabicFontSize" to arabicFontSize.toDouble(),
                    "bengaliFontSize" to bengaliFontSize.toDouble(),
                    "isDarkMode" to isDarkMode,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updateData).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
        }
    }

    suspend fun syncBookmark(bookmark: BookmarkEntity) {
        val uid = requireUserId()
        val docId = "${bookmark.type.lowercase()}_${bookmark.referenceId.replace(":", "_").replace(" ", "_")}"
        val docRef = db.collection("users").document(uid).collection("bookmarks").document(docId)

        try {
            val payload = mapOf(
                "id" to docId,
                "userId" to uid,
                "type" to bookmark.type,
                "referenceId" to bookmark.referenceId,
                "titleBengali" to bookmark.titleBengali,
                "arabicSnippet" to bookmark.arabicSnippet,
                "bengaliSnippet" to bookmark.bengaliSnippet,
                "note" to bookmark.note,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }

    suspend fun syncWorship(worship: DailyWorshipEntity) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("worship").document(worship.dateStr)

        try {
            val payload = mapOf(
                "userId" to uid,
                "dateStr" to worship.dateStr,
                "fajr" to worship.fajr,
                "dhuhr" to worship.dhuhr,
                "asr" to worship.asr,
                "maghrib" to worship.maghrib,
                "isha" to worship.isha,
                "tahajjud" to worship.tahajjud,
                "quranRecited" to worship.quranRecited,
                "fasting" to worship.fasting,
                "sadaqah" to worship.sadaqah
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
        }
    }

    suspend fun submitCloudErrorReport(report: ErrorReportEntity) {
        val uid = requireUserId()
        val docRef = db.collection("error_reports").document()

        try {
            val payload = mapOf(
                "id" to docRef.id,
                "userId" to uid,
                "contentType" to report.contentType,
                "recordId" to report.recordId,
                "issueCategory" to report.issueCategory,
                "userExplanation" to report.userExplanation,
                "referenceSource" to report.referenceSource,
                "status" to "PENDING",
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
        }
    }
}
