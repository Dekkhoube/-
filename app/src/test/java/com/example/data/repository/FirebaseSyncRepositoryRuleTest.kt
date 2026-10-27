package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.BookmarkEntity
import com.example.data.model.DailyWorshipEntity
import com.example.data.model.ErrorReportEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirebaseSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun syncUserProfile_authenticatedUser_createsAndUpdatesProfile() = runBlocking {
        val uid = signInTestUser("testuser@example.com")
        val repo = FirebaseSyncRepository(firestore)

        withTimeout(5000L) {
            repo.syncUserProfile(
                displayName = "Test User",
                currentLanguage = "bn",
                selectedCity = "ঢাকা",
                arabicFontSize = 24.0f,
                bengaliFontSize = 16.0f,
                isDarkMode = false
            )
        }

        val doc = withTimeout(5000L) {
            firestore.collection("users").document(uid).get().await()
        }

        assertTrue(doc.exists())
        assertEquals("Test User", doc.getString("displayName"))
        assertEquals("bn", doc.getString("currentLanguage"))
        assertEquals("ঢাকা", doc.getString("selectedCity"))
    }

    @Test
    fun syncBookmark_authenticatedUser_persistsToSubcollection() = runBlocking {
        val uid = signInTestUser("bookmarkuser@example.com")
        val repo = FirebaseSyncRepository(firestore)

        val bookmark = BookmarkEntity(
            id = 1L,
            type = "QURAN",
            referenceId = "2:255",
            titleBengali = "আয়াতুল কুরসী",
            arabicSnippet = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
            bengaliSnippet = "আল্লাহ, তিনি ছাড়া কোনো সত্য উপাস্য নেই, তিনি চিরঞ্জীব, সর্বসত্তার ধারক।",
            note = "প্রতি নামাজের পর আমল"
        )

        withTimeout(5000L) {
            repo.syncBookmark(bookmark)
        }

        val doc = withTimeout(5000L) {
            firestore.collection("users").document(uid)
                .collection("bookmarks").document("quran_2_255")
                .get().await()
        }

        assertTrue(doc.exists())
        assertEquals("QURAN", doc.getString("type"))
        assertEquals("2:255", doc.getString("referenceId"))
        assertEquals("আয়াতুল কুরসী", doc.getString("titleBengali"))
    }

    @Test
    fun syncWorship_authenticatedUser_updatesDailyRecord() = runBlocking {
        val uid = signInTestUser("worshipuser@example.com")
        val repo = FirebaseSyncRepository(firestore)

        val worship = DailyWorshipEntity(
            dateStr = "2026-10-18",
            fajr = true,
            dhuhr = true,
            asr = true,
            maghrib = true,
            isha = true,
            tahajjud = false,
            quranRecited = true,
            fasting = false,
            sadaqah = true
        )

        withTimeout(5000L) {
            repo.syncWorship(worship)
        }

        val doc = withTimeout(5000L) {
            firestore.collection("users").document(uid)
                .collection("worship").document("2026-10-18")
                .get().await()
        }

        assertTrue(doc.exists())
        assertEquals(true, doc.getBoolean("fajr"))
        assertEquals(true, doc.getBoolean("quranRecited"))
    }

    @Test
    fun unauthenticatedAccess_throwsOrFails() = runBlocking {
        auth.signOut()
        val repo = FirebaseSyncRepository(firestore)

        try {
            repo.syncBookmark(
                BookmarkEntity(
                    id = 2L,
                    type = "HADITH",
                    referenceId = "Bukhari:1",
                    titleBengali = "নিয়তের হাদিস",
                    arabicSnippet = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
                    bengaliSnippet = "প্রত্যেক কাজের ফলাফল নিয়তের উপর নির্ভরশীল।"
                )
            )
            fail("Expected IllegalStateException when not authenticated")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("User must be signed in with Google") == true)
        }
    }
}
