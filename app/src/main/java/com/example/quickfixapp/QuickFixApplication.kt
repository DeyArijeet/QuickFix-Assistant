package com.arijeet.quickfixapp

import android.app.Application
import androidx.work.*
import com.arijeet.quickfixapp.data.QuickFixDatabase
import com.arijeet.quickfixapp.data.QuickFixRepository
import com.arijeet.quickfixapp.service.IssueDetectionWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.TimeUnit

class QuickFixApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { QuickFixDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { QuickFixRepository(database.dao()) }

    override fun onCreate() {
        super.onCreate()
        setupRecurringWork()
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<IssueDetectionWorker>(
            1, TimeUnit.HOURS // Check every hour
        )
        .setConstraints(constraints)
        .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "IssueDetectionWork",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
