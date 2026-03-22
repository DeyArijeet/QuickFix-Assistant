package com.arijeet.quickfixapp.data

import kotlinx.coroutines.flow.Flow

class QuickFixRepository(private val dao: QuickFixDao) {
    val allCategories: Flow<List<Category>> = dao.getAllCategories()
    val history: Flow<List<HistoryEntry>> = dao.getHistory()
    val fixedCount: Flow<Int> = dao.getFixedCount()

    fun getIssuesByCategory(categoryId: String): Flow<List<Issue>> = dao.getIssuesByCategory(categoryId)

    suspend fun getIssueById(issueId: String): Issue? = dao.getIssueById(issueId)

    suspend fun markIssue(issueId: String, status: String) {
        dao.insertHistory(HistoryEntry(issueId = issueId, status = status))
    }
}
