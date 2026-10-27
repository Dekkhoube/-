package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE type = :type ORDER BY timestamp DESC")
    fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE type = :type AND referenceId = :refId)")
    fun isBookmarked(type: String, refId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE type = :type AND referenceId = :refId")
    suspend fun deleteBookmark(type: String, refId: String)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_records")
    fun getAllTasbihRecords(): Flow<List<TasbihDhikrEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: TasbihDhikrEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialRecords(records: List<TasbihDhikrEntity>)
}

@Dao
interface WorshipDao {
    @Query("SELECT * FROM daily_worship WHERE dateStr = :dateStr")
    fun getWorshipForDate(dateStr: String): Flow<DailyWorshipEntity?>

    @Query("SELECT * FROM daily_worship ORDER BY dateStr DESC LIMIT 30")
    fun getRecentWorshipRecords(): Flow<List<DailyWorshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWorship(worship: DailyWorshipEntity)
}

@Dao
interface ErrorReportDao {
    @Query("SELECT * FROM error_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<ErrorReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ErrorReportEntity): Long

    @Query("UPDATE error_reports SET status = :status, reviewerNotes = :notes WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String, notes: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity): Long
}

@Dao
interface CorrectionDao {
    @Query("SELECT * FROM content_corrections ORDER BY timestamp DESC")
    fun getAllProposals(): Flow<List<ContentCorrectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: ContentCorrectionEntity): Long

    @Query("UPDATE content_corrections SET status = :status, reviewedBy = :reviewer WHERE id = :id")
    suspend fun updateProposalStatus(id: Long, status: String, reviewer: String)
}

@Dao
interface AdminContentDao {
    @Query("SELECT * FROM admin_content ORDER BY timestamp DESC")
    fun getAllContent(): Flow<List<AdminContentEntity>>

    @Query("SELECT * FROM admin_content WHERE contentType = :type ORDER BY timestamp DESC")
    fun getContentByType(type: String): Flow<List<AdminContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: AdminContentEntity): Long

    @Query("DELETE FROM admin_content WHERE id = :id")
    suspend fun deleteById(id: Long)
}

