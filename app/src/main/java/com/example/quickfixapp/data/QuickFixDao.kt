package com.arijeet.quickfixapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickFixDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM issues WHERE categoryId = :categoryId")
    fun getIssuesByCategory(categoryId: String): Flow<List<Issue>>

    @Query("SELECT * FROM issues WHERE issueId = :issueId")
    suspend fun getIssueById(issueId: String): Issue?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entry: HistoryEntry)

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<HistoryEntry>>

    @Query("SELECT COUNT(*) FROM history WHERE status = 'Fixed'")
    fun getFixedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssues(issues: List<Issue>)
}
