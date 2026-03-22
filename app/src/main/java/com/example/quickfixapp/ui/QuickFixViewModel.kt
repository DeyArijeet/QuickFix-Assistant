package com.arijeet.quickfixapp.ui

import androidx.lifecycle.*
import com.arijeet.quickfixapp.data.Category
import com.arijeet.quickfixapp.data.Issue
import com.arijeet.quickfixapp.data.QuickFixRepository
import kotlinx.coroutines.launch

class QuickFixViewModel(private val repository: QuickFixRepository) : ViewModel() {

    val allCategories: LiveData<List<Category>> = repository.allCategories.asLiveData()
    val fixedIssuesCount: LiveData<Int> = repository.fixedCount.asLiveData()

    fun getIssuesByCategory(categoryId: String): LiveData<List<Issue>> {
        return repository.getIssuesByCategory(categoryId).asLiveData()
    }

    suspend fun getIssueById(issueId: String): Issue? {
        return repository.getIssueById(issueId)
    }

    fun markIssueFixed(issueId: String, fixed: Boolean) {
        viewModelScope.launch {
            repository.markIssue(issueId, if (fixed) "Fixed" else "Not Fixed")
        }
    }
}

class QuickFixViewModelFactory(private val repository: QuickFixRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuickFixViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuickFixViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
